#include <jni.h>
#include <string>
#include "omp/EquityCalculator.h"
#include <iostream>

std::vector<omp::CardRange> jstr_array_to_card_vec(JNIEnv *pEnv, jobjectArray pArray);

std::string jstr_to_cppstring(JNIEnv *pEnv, jstring pJstring);

jdoubleArray win_to_jdouble_array(JNIEnv *pEnv, const omp::EquityCalculator::Results& cpp_results);

jdoubleArray cpp_double_array_to_jdouble_array(JNIEnv *pEnv, const double pDouble[10]);

extern "C"
JNIEXPORT void JNICALL
Java_com_leslie_cjpokeroddscalculator_calculation_ExactCalc_nativeExactCalc(JNIEnv *env, jobject thiz, jobjectArray cards, jstring board_cards) {
    std::vector<omp::CardRange> cardRangeVec = jstr_array_to_card_vec(env, cards);

    std::string board_cards_cpp = jstr_to_cppstring(env, board_cards);

    omp::EquityCalculator eq;
    bool is_cancelled = false;

    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    jclass jcls = env->GetObjectClass(thiz);
    jmethodID mDuringSimulations = env->GetMethodID(jcls, "during_simulations", "()Z");
    jmethodID mAfterAllSimulations = env->GetMethodID(jcls, "after_all_simulations", "([D[DZ)V");
    jobject jObjGlobal = env->NewGlobalRef(thiz);

    auto callback = [&eq, &is_cancelled, &jvm, &jObjGlobal, &mDuringSimulations](const omp::EquityCalculator::Results& results) {
        JNIEnv* myNewEnv;
        jvm->AttachCurrentThread(&myNewEnv, nullptr);

        if ((1.0 / results.progress - 1.0) * results.time > 60.0) {
            is_cancelled = true;
        }

        if ((myNewEnv->CallBooleanMethod(jObjGlobal, mDuringSimulations) == JNI_FALSE) || is_cancelled) {
            eq.stop();
        }

        jvm->DetachCurrentThread();
    };

    eq.start(
        cardRangeVec,
        omp::CardRange::getCardMask(board_cards_cpp),
        omp::CardRange::getCardMask(""),
        true,
        0,
        callback,
        0.3,
        0
    );

    eq.wait();
    auto r = eq.getResults();

    jdoubleArray equity = cpp_double_array_to_jdouble_array(env, r.equity);
    jdoubleArray win = win_to_jdouble_array(env, r);

    env->CallVoidMethod(thiz, mAfterAllSimulations, equity, win, is_cancelled ? JNI_TRUE : JNI_FALSE);

    env->DeleteLocalRef(equity);
    env->DeleteLocalRef(win);

    env->DeleteGlobalRef(jObjGlobal);
    env->DeleteLocalRef(jcls);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_leslie_cjpokeroddscalculator_calculation_MonteCarloCalc_nativeMonteCarloCalc(JNIEnv *env, jobject thiz, jobjectArray cards, jstring board_cards) {
    std::vector<omp::CardRange> cardRangeVec = jstr_array_to_card_vec(env, cards);

    std::string board_cards_cpp = jstr_to_cppstring(env, board_cards);

    omp::EquityCalculator eq;

    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    jclass jcls = env->GetObjectClass(thiz);
    jmethodID mDuringSimulations = env->GetMethodID(jcls, "during_simulations", "([D[D)Z");
    jmethodID mAfterAllSimulations = env->GetMethodID(jcls, "after_all_simulations", "([D[D)V");
    jobject jObjGlobal = env->NewGlobalRef(thiz);

    auto callback = [&eq, &jvm, &jObjGlobal, &mDuringSimulations](const omp::EquityCalculator::Results& results) {
        JNIEnv* myNewEnv;
        jvm->AttachCurrentThread(&myNewEnv, nullptr);

        jdoubleArray jequity = cpp_double_array_to_jdouble_array(myNewEnv, results.equity);
        jdoubleArray jwin = win_to_jdouble_array(myNewEnv, results);

        if (myNewEnv->CallBooleanMethod(jObjGlobal, mDuringSimulations, jequity, jwin) == JNI_FALSE) {
            eq.stop();
        }

        myNewEnv->DeleteLocalRef(jequity);
        myNewEnv->DeleteLocalRef(jwin);

        jvm->DetachCurrentThread();
    };

    eq.start(
        cardRangeVec,
        omp::CardRange::getCardMask(board_cards_cpp),
        omp::CardRange::getCardMask(""),
        false,
        1e-9,
        callback,
        0.3,
        1
    );

    eq.wait();
    auto r = eq.getResults();

    jdoubleArray equity = cpp_double_array_to_jdouble_array(env, r.equity);
    jdoubleArray win = win_to_jdouble_array(env, r);

    env->CallVoidMethod(thiz, mAfterAllSimulations, equity, win);

    env->DeleteLocalRef(equity);
    env->DeleteLocalRef(win);

    env->DeleteGlobalRef(jObjGlobal);
    env->DeleteLocalRef(jcls);
}

jdoubleArray win_to_jdouble_array(JNIEnv *pEnv, const omp::EquityCalculator::Results& cpp_results) {
    double win[10];
    for (int i=0; i < 10; ++i) {
        win[i] = cpp_results.wins[i] / (double) cpp_results.hands;
    }

    return cpp_double_array_to_jdouble_array(pEnv, win);
}

jdoubleArray cpp_double_array_to_jdouble_array(JNIEnv *pEnv, const double pDouble[10]) {
    jdoubleArray jresult = pEnv->NewDoubleArray(10);

    pEnv->SetDoubleArrayRegion(jresult, 0, 10, &pDouble[0]);

    return jresult;
}

std::string jstr_to_cppstring(JNIEnv *pEnv, jstring pJstring) {
    const jsize strLen = pEnv->GetStringUTFLength(pJstring);
    const char *charBuffer = pEnv->GetStringUTFChars(pJstring, (jboolean *) nullptr);
    std::string cpp_str(charBuffer, strLen);
    pEnv->ReleaseStringUTFChars(pJstring, charBuffer);
    pEnv->DeleteLocalRef(pJstring);

    return cpp_str;
}

std::vector<omp::CardRange> jstr_array_to_card_vec(JNIEnv *pEnv, jobjectArray pArray) {
    std::vector<omp::CardRange> cardRangeVec;

    // Get length
    int len = pEnv->GetArrayLength(pArray);

    for (int i=0; i<len; i++) {
        // Cast array element to string
        auto jstr = (jstring) (pEnv->GetObjectArrayElement(pArray, i));

        // Convert Java string to std::string
        std::string cpp_str = jstr_to_cppstring(pEnv, jstr);

        // Push back string to vector
        cardRangeVec.emplace_back(cpp_str);
    }

    return cardRangeVec;
}



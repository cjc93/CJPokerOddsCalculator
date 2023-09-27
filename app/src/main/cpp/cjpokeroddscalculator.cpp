#include <jni.h>
#include <string>
#include "omp/EquityCalculator.h"
#include <iostream>

std::vector<omp::CardRange> jstr_array_to_card_vec(JNIEnv *pEnv, jobjectArray pArray);

std::string jstr_to_cppstring(JNIEnv *pEnv, jstring pJstring);

jdoubleArray results_to_jdouble_array(JNIEnv *pEnv, omp::EquityCalculator::Results cpp_results);

extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_leslie_cjpokeroddscalculator_calculation_ExactCalc_nativeExactCalc(JNIEnv *env, jobject thiz, jobjectArray cards, jstring board_cards) {
    std::vector<omp::CardRange> cardRangeVec = jstr_array_to_card_vec(env, cards);

    std::string board_cards_cpp = jstr_to_cppstring(env, board_cards);

    omp::EquityCalculator eq;

    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    jclass jcls = env->GetObjectClass(thiz);
    jmethodID mDuringSimulations = env->GetMethodID(jcls, "during_simulations", "()Z");
    jobject jObjGlobal = env->NewGlobalRef(thiz);

    auto callback = [&eq, &jvm, &jObjGlobal, &mDuringSimulations](const omp::EquityCalculator::Results& results) {
        JNIEnv* myNewEnv;
        jvm->AttachCurrentThread(&myNewEnv, nullptr);

        if (myNewEnv->CallBooleanMethod(jObjGlobal, mDuringSimulations) == JNI_FALSE) {
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
        2
    );

    eq.wait();
    auto r = eq.getResults();

    jdoubleArray result = results_to_jdouble_array(env, r);

    env->DeleteGlobalRef(jObjGlobal);
    env->DeleteLocalRef(jcls);

    return result;
}

extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_leslie_cjpokeroddscalculator_calculation_MonteCarloCalc_nativeMonteCarloCalc(JNIEnv *env, jobject thiz, jobjectArray cards, jstring board_cards) {
    std::vector<omp::CardRange> cardRangeVec = jstr_array_to_card_vec(env, cards);

    std::string board_cards_cpp = jstr_to_cppstring(env, board_cards);

    omp::EquityCalculator eq;

    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    jclass jcls = env->GetObjectClass(thiz);
    jmethodID mDuringSimulations = env->GetMethodID(jcls, "during_simulations", "([D)Z");
    jobject jObjGlobal = env->NewGlobalRef(thiz);

    auto callback = [&eq, &jvm, &jObjGlobal, &mDuringSimulations](const omp::EquityCalculator::Results& results) {
        JNIEnv* myNewEnv;
        jvm->AttachCurrentThread(&myNewEnv, nullptr);

        jdoubleArray jresult = results_to_jdouble_array(myNewEnv, results);

        if (myNewEnv->CallBooleanMethod(jObjGlobal, mDuringSimulations, jresult) == JNI_FALSE) {
            eq.stop();
        }

        myNewEnv->DeleteLocalRef(jresult);

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

    jdoubleArray result = results_to_jdouble_array(env, r);

    env->DeleteGlobalRef(jObjGlobal);
    env->DeleteLocalRef(jcls);

    return result;
}

jdoubleArray results_to_jdouble_array(JNIEnv *pEnv, omp::EquityCalculator::Results cpp_results) {
    jdoubleArray jresult = pEnv->NewDoubleArray(10);

    pEnv->SetDoubleArrayRegion(jresult, 0, 10, &cpp_results.equity[0]);

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



package com.leslie.cjpokeroddscalculator.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowMetrics;
import com.google.android.material.imageview.ShapeableImageView;

import android.widget.Space;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.shape.ShapeAppearanceModel;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AndroidStatic {

    public static void navControllerNavigateWithArgs(Fragment fragment, int currentFragmentId, int actionId, Bundle args) {
        NavController navController = NavHostFragment.findNavController(fragment);
        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == currentFragmentId) {
            navController.navigate(actionId, args);
        }
    }

    public static DisplayMetrics getDisplayMetrics(FragmentActivity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        displayMetrics.widthPixels = bounds.width();
        displayMetrics.heightPixels = bounds.height();
        return displayMetrics;
    }

    public static void setCardSize(List<ShapeableImageView> cardButtons, int boardCardMaxHeight, int cardMaxWidth) {
        for (int i = 0; i < cardButtons.size(); i++) {
            cardButtons.get(i).setMaxHeight(boardCardMaxHeight);
            cardButtons.get(i).setMaxWidth(cardMaxWidth);
        }
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static List<ShapeableImageView> createOmahaCardButtons(ViewBinding binding, ConstraintLayout playerRow, Space spaceAboveCards, Space spaceBelowCards, int cardsPerHand) {
        List<ShapeableImageView> cardList = new ArrayList<>();

        for (int i = 0; i < cardsPerHand; i++) {
            ShapeableImageView card = new ShapeableImageView(binding.getRoot().getContext(), null, 0);
            card.setId(View.generateViewId());
            card.setScaleType(ShapeableImageView.ScaleType.FIT_CENTER);
            card.setAdjustViewBounds(true);
            int dp5 = dpToPx(binding.getRoot().getContext(), 5);
            card.setPadding(dp5, dp5, dp5, dp5);
            card.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(card.getContext(), R.color.card_selected)));
            card.setShapeAppearanceModel(new ShapeAppearanceModel.Builder().setAllCornerSizes(dp5).build());
            cardList.add(card);
        }

        for (int i = 0; i < cardsPerHand; i++) {
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );

            if (i == 0) {
                layoutParams.topToBottom = spaceAboveCards.getId();
                layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                layoutParams.rightToLeft = cardList.get(i + 1).getId();
            } else if (i == cardsPerHand - 1) {
                layoutParams.topToBottom = spaceAboveCards.getId();
                layoutParams.leftToRight = cardList.get(i - 1).getId();
                layoutParams.rightToRight = spaceAboveCards.getId();
            } else {
                layoutParams.topToBottom = spaceAboveCards.getId();
                layoutParams.leftToRight = cardList.get(i - 1).getId();
                layoutParams.rightToLeft = cardList.get(i + 1).getId();
            }

            cardList.get(i).setLayoutParams(layoutParams);

            playerRow.addView(cardList.get(i));
        }

        ConstraintLayout.LayoutParams spaceLayoutParams = (ConstraintLayout.LayoutParams) spaceBelowCards.getLayoutParams();
        spaceLayoutParams.topToBottom = cardList.get(0).getId();
        spaceBelowCards.setLayoutParams(spaceLayoutParams);

        return cardList;
    }

    public static void setCardRowImages(List<ShapeableImageView> cardList, SpecificCardsRow specificCardsRow) {
        for (int cardIdx = 0; cardIdx < specificCardsRow.cards.length; cardIdx++) {
            String cardStr = specificCardsRow.cards[cardIdx];
            Integer id = suitRankDrawableMap.get(cardStr);
            if (id != null) {
                cardList.get(cardIdx).setImageResource(id);
            }

            if (specificCardsRow.selectedCard != null && specificCardsRow.selectedCard == cardIdx) {
                cardList.get(cardIdx).setStrokeWidth(dpToPx(cardList.get(cardIdx).getContext(), 5));
            } else {
                cardList.get(cardIdx).setStrokeWidth(0);
            }
        }
    }

    public static Map<String, Integer> suitRankDrawableMap = new HashMap<>();
    static {
        suitRankDrawableMap.put("", R.drawable.unknown_button);

        suitRankDrawableMap.put("2d", R.drawable.d2);
        suitRankDrawableMap.put("3d", R.drawable.d3);
        suitRankDrawableMap.put("4d", R.drawable.d4);
        suitRankDrawableMap.put("5d", R.drawable.d5);
        suitRankDrawableMap.put("6d", R.drawable.d6);
        suitRankDrawableMap.put("7d", R.drawable.d7);
        suitRankDrawableMap.put("8d", R.drawable.d8);
        suitRankDrawableMap.put("9d", R.drawable.d9);
        suitRankDrawableMap.put("Td", R.drawable.d10);
        suitRankDrawableMap.put("Jd", R.drawable.d11);
        suitRankDrawableMap.put("Qd", R.drawable.d12);
        suitRankDrawableMap.put("Kd", R.drawable.d13);
        suitRankDrawableMap.put("Ad", R.drawable.d14);

        suitRankDrawableMap.put("2c", R.drawable.c2);
        suitRankDrawableMap.put("3c", R.drawable.c3);
        suitRankDrawableMap.put("4c", R.drawable.c4);
        suitRankDrawableMap.put("5c", R.drawable.c5);
        suitRankDrawableMap.put("6c", R.drawable.c6);
        suitRankDrawableMap.put("7c", R.drawable.c7);
        suitRankDrawableMap.put("8c", R.drawable.c8);
        suitRankDrawableMap.put("9c", R.drawable.c9);
        suitRankDrawableMap.put("Tc", R.drawable.c10);
        suitRankDrawableMap.put("Jc", R.drawable.c11);
        suitRankDrawableMap.put("Qc", R.drawable.c12);
        suitRankDrawableMap.put("Kc", R.drawable.c13);
        suitRankDrawableMap.put("Ac", R.drawable.c14);

        suitRankDrawableMap.put("2h", R.drawable.h2);
        suitRankDrawableMap.put("3h", R.drawable.h3);
        suitRankDrawableMap.put("4h", R.drawable.h4);
        suitRankDrawableMap.put("5h", R.drawable.h5);
        suitRankDrawableMap.put("6h", R.drawable.h6);
        suitRankDrawableMap.put("7h", R.drawable.h7);
        suitRankDrawableMap.put("8h", R.drawable.h8);
        suitRankDrawableMap.put("9h", R.drawable.h9);
        suitRankDrawableMap.put("Th", R.drawable.h10);
        suitRankDrawableMap.put("Jh", R.drawable.h11);
        suitRankDrawableMap.put("Qh", R.drawable.h12);
        suitRankDrawableMap.put("Kh", R.drawable.h13);
        suitRankDrawableMap.put("Ah", R.drawable.h14);

        suitRankDrawableMap.put("2s", R.drawable.s2);
        suitRankDrawableMap.put("3s", R.drawable.s3);
        suitRankDrawableMap.put("4s", R.drawable.s4);
        suitRankDrawableMap.put("5s", R.drawable.s5);
        suitRankDrawableMap.put("6s", R.drawable.s6);
        suitRankDrawableMap.put("7s", R.drawable.s7);
        suitRankDrawableMap.put("8s", R.drawable.s8);
        suitRankDrawableMap.put("9s", R.drawable.s9);
        suitRankDrawableMap.put("Ts", R.drawable.s10);
        suitRankDrawableMap.put("Js", R.drawable.s11);
        suitRankDrawableMap.put("Qs", R.drawable.s12);
        suitRankDrawableMap.put("Ks", R.drawable.s13);
        suitRankDrawableMap.put("As", R.drawable.s14);
    }
}

package com.leslie.cjpokeroddscalculator.adapter;

public interface PlayerRowInteractionListener {
    void onRemovePlayer(int rowIdx);
    void onToggleStats(int rowIdx);
    void onSelectCard(int rowIdx, int cardIdx);
    void onHideCardSelector();
    void onToggleRangeHand(int rowIdx);
    void onShowRangeSelector(int rowIdx);
}
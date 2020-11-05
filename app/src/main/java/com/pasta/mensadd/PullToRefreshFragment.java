package com.pasta.mensadd;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PullToRefreshFragment extends Fragment {
    private static final float PULL_REFRESH_THRESHOLD = 80;
    protected TextView mRefreshText;
    protected RecyclerView mRecyclerView;
    private String mWannaRefreshText;
    private String mReleaseToRefreshText;

    public void setUpPullToRefresh(int wannaRefreshId, int releaseToRefreshId) {
        this.mWannaRefreshText = getString(wannaRefreshId);
        this.mReleaseToRefreshText = getString(releaseToRefreshId);
        final float pullRefreshThreshold = PULL_REFRESH_THRESHOLD * requireActivity().getResources().getDisplayMetrics().density;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                ValueAnimator animator = Utils.createPullToRefreshAnimator(mRecyclerView);
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING && mRecyclerView.getPaddingTop() > 0 && mRecyclerView.getPaddingTop() < pullRefreshThreshold ) {
                    animator.start();
                } else if (mRecyclerView.getPaddingTop() >= pullRefreshThreshold) {
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            onRefresh();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    animator.start();
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mRecyclerView.getPaddingTop() > 0) {
                    mRecyclerView.setPadding(0, Math.max(0, mRecyclerView.getPaddingTop() - dy), 0, 0);
                    if (mRecyclerView.getPaddingTop() < pullRefreshThreshold) {
                        mRefreshText.setText(mWannaRefreshText);
                    }
                }
                if (mRecyclerView.getPaddingTop() < 5) {
                    mRefreshText.setVisibility(View.INVISIBLE);
                }
            }
        });

        mRecyclerView.setLayoutManager(getRecyclerViewLayoutManager(pullRefreshThreshold));

    }

    public LinearLayoutManager getRecyclerViewLayoutManager(float pullRefreshThreshold) {
        return new LinearLayoutManager(requireActivity()) {
            @Override
            public int scrollVerticallyBy ( int dy, RecyclerView.Recycler recycler,
                                            RecyclerView.State state ) {
                int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
                int overScroll = dy - scrollRange;
                if (overScroll < 0) {
                    if (mRecyclerView.getPaddingTop() < pullRefreshThreshold && mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
                        mRecyclerView.setPadding(0, mRecyclerView.getPaddingTop() + Math.abs(overScroll), 0, 0);
                        mRefreshText.setText(mWannaRefreshText);
                        mRefreshText.setVisibility(View.VISIBLE);
                    } else if (mRecyclerView.getPaddingTop() >= pullRefreshThreshold) {
                        mRecyclerView.setPadding(0, mRecyclerView.getPaddingTop() + Math.abs(overScroll), 0, 0);
                        mRefreshText.setText(mReleaseToRefreshText);
                    }
                }
                return scrollRange;
            }
        };
    }

    public void onRefresh() {
        mRefreshText.setVisibility(View.INVISIBLE);
    }


}

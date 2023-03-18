package com.example.thenewappdemo;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class BaseBackPressedListener implements OnBackPressedListener{
 private final FragmentActivity activity;
 public BaseBackPressedListener(FragmentActivity activity){
     this.activity = activity;

 }

    @Override
    public void doBack() {
        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }
}

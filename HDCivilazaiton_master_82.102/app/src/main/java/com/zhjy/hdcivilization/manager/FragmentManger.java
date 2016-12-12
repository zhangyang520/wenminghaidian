package com.zhjy.hdcivilization.manager;


import com.zhjy.hdcivilization.fragment.CiviCommentHotFragment;
import com.zhjy.hdcivilization.fragment.CiviCommentMineFragment;
import com.zhjy.hdcivilization.fragment.CommentJoinFragment;
import com.zhjy.hdcivilization.fragment.CommentSubFragment;
import com.zhjy.hdcivilization.fragment.SuperviseMineListFragment;
import com.zhjy.hdcivilization.fragment.SuperviseProblemFragment;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Administrator on 2016/7/28.
 */
public class FragmentManger {
    public enum FragmentEnum{
        SuperViseMine("superviseMine"),SuperViseProblem("superviseProblem"),
                        CivilCommentHot("civilCommentHot"),CivilCommentMine("civilCommentMine"),CommentJoin("commentJoin"),
        CommentSub("commentSub");
        private String name;
        FragmentEnum(String name){
            this.name=name;
        }

        public String getName(){
            return this.name;
        }
    }

    private static FragmentManger instance;
    private static Map<String,Fragment> fragmentMap=new HashMap<String,Fragment>();

    /**
     * 构造函数私有化
     */
    private FragmentManger(){

    }

    public static FragmentManger getInstance(){
        if(instance==null){
            synchronized (FragmentManger.class){
                if (instance==null){
                    instance=new FragmentManger();
                }
            }
        }
        return  instance;
    }

    public Fragment getFragment(String fragmentName){
        if(fragmentMap.get(fragmentName)==null){
            synchronized (FragmentManger.class){
                if(fragmentMap.get(fragmentName)==null){
                    if("superviseMine".equals(fragmentName)){
                        fragmentMap.put(fragmentName,new SuperviseMineListFragment());
                    }else if("superviseProblem".equals(fragmentName)){
                        fragmentMap.put(fragmentName,new SuperviseProblemFragment());
                    }else if("civilCommentHot".equals(fragmentName)){
                        fragmentMap.put(fragmentName,new CiviCommentHotFragment());
                    }else if("civilCommentMine".equals(fragmentName)){
                        fragmentMap.put(fragmentName,new CiviCommentMineFragment());
                    }else if ("commentJoin".equals(fragmentName)){
                        fragmentMap.put(fragmentName,new CommentJoinFragment());
                    }else if ("commentSub".equals(fragmentName)){
                        fragmentMap.put(fragmentName,new CommentSubFragment());
                    }
                }
            }
        }else{
            if(fragmentMap.get(fragmentName).isAdded()) {
                synchronized (FragmentManger.class) {
                    if(fragmentMap.get(fragmentName).isAdded()) {
                        if ("superviseMine".equals(fragmentName)) {
                            fragmentMap.put(fragmentName, new SuperviseMineListFragment());
                        } else if ("superviseProblem".equals(fragmentName)) {
                            fragmentMap.put(fragmentName, new SuperviseProblemFragment());
                        } else if ("civilCommentHot".equals(fragmentName)) {
                            fragmentMap.put(fragmentName, new CiviCommentHotFragment());
                        } else if ("civilCommentMine".equals(fragmentName)) {
                            fragmentMap.put(fragmentName, new CiviCommentMineFragment());
                        } else if ("commentJoin".equals(fragmentName)) {
                            fragmentMap.put(fragmentName, new CommentJoinFragment());
                        } else if ("commentSub".equals(fragmentName)) {
                            fragmentMap.put(fragmentName, new CommentSubFragment());
                        }
                    }
                }
            }
        }
        return fragmentMap.get(fragmentName);
    }
}

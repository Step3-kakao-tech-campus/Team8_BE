package com.kakao.techcampus.wekiki._core.utils;


import com.kakao.techcampus.wekiki.post.Post;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class IndexUtils {

    public HashMap<Long, String> createIndex(List<Post> posts){
        HashMap<Long, ArrayList<Long>> tree = new HashMap<>();
        HashMap<Long,String> indexs = new HashMap<>();

        for(Post p : posts){
            // 만약 부모가 존재하면
            if(p.getParent() != null){
                if(tree.containsKey(p.getParent().getId())){
                    tree.get(p.getParent().getId()).add(p.getId());
                }else{
                    tree.put(p.getParent().getId(),new ArrayList<>(Arrays.asList(p.getId())));
                }
            }
        }

        int res = 1;

        for(Post p : posts){
            // 루트일때
            if(p.getParent() == null){
                DFS(res,p.getId(),"", indexs , tree);
                res++;
            }
        }

        return indexs;
    }

    public void DFS(int res, Long now, String index , HashMap<Long,String> indexs , HashMap<Long, ArrayList<Long>> tree){
        indexs.put(now,index+res);
        if(tree.containsKey(now)){
            for(int i = 0 ; i < tree.get(now).size(); i++){
                DFS(i+1,tree.get(now).get(i), index+res+"-",indexs,tree);
            }
        }
    }

}

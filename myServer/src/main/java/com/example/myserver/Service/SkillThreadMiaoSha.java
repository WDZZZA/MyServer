package com.example.myserver.Service;

import com.example.myserver.Request.MiaoShaReq;

public class SkillThreadMiaoSha extends Thread {

    private ProductService productService;
//自定义了一个线程
    public SkillThreadMiaoSha(ProductService productService, String skillThreadName) {
        super(skillThreadName);
        this.productService = productService;
    }

    @Override
    public void run() {
        productService.seckill(new MiaoShaReq());
    }
}

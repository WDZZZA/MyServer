package com.example.myserver.Service;

public class SkillThread extends Thread {

    private SkillService skillService;
//自定义了一个线程
    public SkillThread(SkillService skillService, String skillThreadName) {
        super(skillThreadName);
        this.skillService = skillService;
    }

    @Override
    public void run() {
        skillService.seckill();
    }
}

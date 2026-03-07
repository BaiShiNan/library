package com.library.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.library.backend.mapper")
@EnableCaching
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
                // 后端启动标识符
        System.out.println("=".repeat(60) + "后端启动" + "=".repeat(60) +
                "                                                                                                                \n" +
                "                                                                                ,--.                            \n" +
                "    ,---,.                            .--.--.      ,---,                      ,--.'|                            \n" +
                "  ,'  .'  \\                 ,--,     /  /    '.  ,--.' |       ,--,       ,--,:  : |                            \n" +
                ",---.' .' |               ,--.'|    |  :  /`. /  |  |  :     ,--.'|    ,`--.'`|  ' :                     ,---,  \n" +
                "|   |  |: |               |  |,     ;  |  |--`   :  :  :     |  |,     |   :  :  | |                 ,-+-. /  | \n" +
                ":   :  :  /    ,--.--.    `--'_     |  :  ;_     :  |  |,--. `--'_     :   |   \\ | :    ,--.--.     ,--.'|'   | \n" +
                ":   |    ;    /       \\   ,' ,'|     \\  \\    `.  |  :  '   | ,' ,'|    |   : '  '; |   /       \\   |   |  ,\"' | \n" +
                "|   :     \\  .--.  .-. |  '  | |      `----.   \\ |  |   /' : '  | |    '   ' ;.    ;  .--.  .-. |  |   | /  | | \n" +
                "|   |   . |   \\__\\/: . .  |  | :      __ \\  \\  | '  :  | | | |  | :    |   | | \\   |   \\__\\/: . .  |   | |  | | \n" +
                "'   :  '; |   ,\" .--.; |  '  : |__   /  /`--'  / |  |  ' | : '  : |__  '   : |  ; .'   ,\" .--.; |  |   | |  |/  \n" +
                "|   |  | ;   /  /  ,.  |  |  | '.'| '--'.     /  |  :  :_:,' |  | '.'| |   | '`--'    /  /  ,.  |  |   | |--'   \n" +
                "|   :   /   ;  :   .'   \\ ;  :    ;   `--'---'   |  | ,'     ;  :    ; '   : |       ;  :   .'   \\ |   |/       \n" +
                "|   | ,'    |  ,     .-./ |  ,   /               `--''       |  ,   /  ;   |.'       |  ,     .-./ '---'        \n" +
                "`----'       `--`---'      ---`-'                             ---`-'   '---'          `--`---'                  \n" +
                "                                                                                                                ");
    }

}

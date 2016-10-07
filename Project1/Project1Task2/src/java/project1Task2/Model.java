package project1Task2;

import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shuang
 */
public class Model {
    HashMap<String,Integer> option_count;
    String option = null;

    public Model(){
        this.option_count = new HashMap<String,Integer>();
        option_count.put("A",0);
        option_count.put("B", 0);
        option_count.put("C", 0);
        option_count.put("D", 0);
    }
        
    public void countOption(String s) {
        option_count.put(s, option_count.get(s)+1);
    }
    
    public HashMap<String,Integer> getOptionCount() {
        return option_count;
    }
    
    public void setOption(String option) {
        this.option=option;
        countOption(option);  
        
    }
    
    public String getOption() {
        return option;
    }
    
    public void clear() {
        option_count.put("A",0);
        option_count.put("B", 0);
        option_count.put("C", 0);
        option_count.put("D", 0);
    }
}

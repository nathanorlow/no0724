package org.nateorlow.tool;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String,Tool> codeToTool;

    public Inventory(){
        codeToTool = new HashMap<>();
    }
    public void addTool(Tool tool){
        codeToTool.put(tool.getCode(), tool);
    }

    public Tool lookupToolByCode(String toolCode){
        return codeToTool.get(toolCode);
    }
}

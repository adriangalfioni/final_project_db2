/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdb2;

import java.util.LinkedList;

/**
 *
 * @author adrian
 */
public class Table {
    
    private String name;
    private LinkedList<String[]> columns;
    private LinkedList<String> pks;
    private LinkedList<String> fks;
    private LinkedList<String> uqks;
    private LinkedList<String[]> triggers;
    private LinkedList<String[]> indexs;

    public Table() {
        name = null;
        columns = new LinkedList<String[]>();
        pks = new LinkedList<String>();
        fks = new LinkedList<String>();
        uqks = new LinkedList<String>();
        triggers = new LinkedList<String[]>();
        indexs = new LinkedList<String[]>();
    }

    public String getName() {
        return name;
    }

    public LinkedList<String[]> getColumns() {
        return columns;
    }

    public LinkedList<String> getPks() {
        return pks;
    }

    public LinkedList<String> getFks() {
        return fks;
    }

    public LinkedList<String> getUqks() {
        return uqks;
    }

    public LinkedList<String[]> getTriggers() {
        return triggers;
    }

    public LinkedList<String[]> getIndexs() {
        return indexs;
    }
    
    public void setName(String name){
        this.name=name;
    }
    
    public void addColumn(String name, String type){
        String[] col = new String[2];
        col[0]=name;
        col[1]=type;
        columns.add(col);
    }
    
    public void addIndex(String indexName, String unique, String columnName, String ascDesc){
        String[] col = new String[4];
        col[0]=indexName;
        col[1]=unique;
        col[2]=columnName;
        col[3]=ascDesc;
        indexs.add(col);
    }
    
    public void addTrigger(String name, String timing, String condition, String on_table){
        String[] col = new String[4];
        col[0]=name;
        col[1]=timing;
        col[2]=condition;
        col[3]=on_table;
        triggers.add(col);
    }
    
    
    public void addPk(String columnName){
        pks.add(columnName);
    }
    
    public void addFk(String columnName){
        fks.add(columnName);
    }
    
    public void addUqk(String columnName){
        uqks.add(columnName);
    }
    
    public boolean equals(Table tlbToComp){
        if(!name.equalsIgnoreCase(tlbToComp.getName())){
            return false;
        }
        if(!equalsKeys(pks, tlbToComp.getPks())){
            return false;
        }
        if(!equalsKeys(fks, tlbToComp.getFks())){
            return false;
        }
        if(!equalsKeys(uqks, tlbToComp.getUqks())){
            return false;
        }
        if(!equalsLA(columns, tlbToComp.getColumns())){
            return false;
        }
        if(!equalsLA(indexs, tlbToComp.getIndexs())){
            return false;
        }
        if(!equalsLA(triggers, tlbToComp.getTriggers())){
            return false;
        }
        
        return true;
    }
    
    private boolean equalsKeys(LinkedList<String> fstK, LinkedList<String> scdK){
        if(fstK.size() != scdK.size()){
            return false;
        }
        for (int i = 0; i < fstK.size(); i++) {
            boolean eq = false;
            String key = fstK.get(i);
            for (int j = 0; j < scdK.size(); j++) {
                if(key.equalsIgnoreCase(scdK.get(j))) eq=true;
            }
            if(eq==false) return false;
        }
        return true;
    }
    
    private boolean equalsLA(LinkedList<String[]> fstLA, LinkedList<String[]> scLA){
        if(fstLA.size() != scLA.size()){
            return false;
        }
        for (int i = 0; i < fstLA.size(); i++) {
            boolean find = false;
            String[] elem = fstLA.get(i);
            for (int j = 0; j < scLA.size(); j++) {
                String[] elem2 = fstLA.get(i);
                if(elem[0].equalsIgnoreCase(elem2[0])){
                    find=true;
                    for (int k = 1; k < elem.length; k++) {
                        if(!elem[k].equalsIgnoreCase(elem2[k])) return false; 
                    }
                }
            }
            if(find==false) return false;
        }
        return true;
    }
}

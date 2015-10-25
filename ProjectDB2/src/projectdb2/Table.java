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
        columns.add(col);
    }
    
    public void addTrigger(String name, String condition){
        String[] col = new String[2];
        col[0]=name;
        col[1]=condition;
        columns.add(col);
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
    
}

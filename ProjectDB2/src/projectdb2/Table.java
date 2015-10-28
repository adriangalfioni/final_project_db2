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
    private LinkedList<String[]> fks;
    private LinkedList<String> uqks;
    private LinkedList<String[]> triggers;
    private LinkedList<String[]> indexs;

    public Table() {
        name = null;
        columns = new LinkedList<String[]>();
        pks = new LinkedList<String>();
        fks = new LinkedList<String[]>();
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

    public LinkedList<String[]> getFks() {
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
    
    public void addFk(String columnName, String tableRef){
        String[] col = new String[2];
        col[0]=columnName;
        col[1]=tableRef;
        fks.add(col);
    }
    
    public void addUqk(String columnName){
        uqks.add(columnName);
    }
    
    public int findTable(LinkedList<Table> tbls){
        for (int i = 0; i < tbls.size(); i++) {
            if(this.name.equalsIgnoreCase(tbls.get(i).getName())){
                return i;
            }
        }
        return -1;
    }
    
    public String checkDif(Table tlbToComp){
        String report = "";
        if(!name.equalsIgnoreCase(tlbToComp.getName())){
            return report;
        }else{
            report +=equalsPorUKeys(pks, tlbToComp.getPks(),"Primary Key");
            report +=equalsPorUKeys(uqks, tlbToComp.getUqks(),"Unique Key");
            report += equalsLA(columns, tlbToComp.getColumns(),"attribute");
            report += equalsLA(triggers, tlbToComp.getTriggers(),"trigger");
            report += equalsLA(indexs, tlbToComp.getIndexs(),"index");
            report += equalsLA(fks, tlbToComp.getFks(),"fk");
            /*if(!equalsKeys(fks, tlbToComp.getFks())){
                return report;
            }
            if(!equalsKeys(uqks, tlbToComp.getUqks())){
                return report;
            }
            if(!equalsLA(columns, tlbToComp.getColumns())){
                return report;
            }
            if(!equalsLA(indexs, tlbToComp.getIndexs())){
                return report;
            }
            if(!equalsLA(triggers, tlbToComp.getTriggers())){
                return report;
            }*/

            if(report.isEmpty()){
                report="La tabla "+name+" es igual en ambas Bases de Datos";
            }

            return report;
        }
    }
    
    private String equalsPorUKeys(LinkedList<String> fstK, LinkedList<String> scdK, String wichKey){
        String rep = "";
        for (int i = 0; i < fstK.size(); i++) {
            boolean eq = false;
            String key = fstK.get(i);      
            for (int j = 0; j < scdK.size(); j++) {
                if(key.equalsIgnoreCase(scdK.get(j))) eq=true;
            }
            if(eq==false) rep=rep+"El atributo "+key+" no se encuentra en la "+ wichKey +" de "+DBComparator.scdSchema+"."+name+"\n";
        }
        for (int i = 0; i < scdK.size(); i++) {
            boolean eq = false;
            String key = scdK.get(i);
            for (int j = 0; j < fstK.size(); j++) {
                if(key.equalsIgnoreCase(fstK.get(j))) eq=true;
            }
            if(eq==false) rep=rep+"El atributo "+key+" no se encuentra en la "+ wichKey + " de "+DBComparator.fstSchema+"."+name+"\n";
        }
        return rep;
    }
    
    private String equalsLA(LinkedList<String[]> fstLA, LinkedList<String[]> scLA, String type){
        String rep = "";
        for (int i = 0; i < fstLA.size(); i++) {
            boolean find = false;
            String[] elem = fstLA.get(i);
            for (int j = 0; j < scLA.size(); j++) {
                String[] elem2 = scLA.get(j);
                if(elem[0].equalsIgnoreCase(elem2[0])){
                    find=true;
                    if(type.equalsIgnoreCase("attribute")){
                        if(!elem[1].equalsIgnoreCase(elem2[1])) 
                        rep += "El atributo " + elem[0] + " en " + DBComparator.fstSchema + "." + name + " es de tipo "
                                + elem[1]+ " y en " + DBComparator.scdSchema + "." + name + " es de tipo " + elem2[1]+"\n"; 
                    }
                    if(type.equalsIgnoreCase("fk")){
                        if(!elem[1].equalsIgnoreCase(elem2[1])) 
                        rep += "La Foreign Key definida sobre " + elem[0] + " en " + DBComparator.fstSchema + "." + name + " hace referencia a la tabla "
                                + elem[1]+ " y en " + DBComparator.scdSchema + "." + name + " a la tabla " + elem2[1]+"\n"; 
                    }
                    if(type.equalsIgnoreCase("trigger")){
                        if(!elem[1].equalsIgnoreCase(elem2[1])){
                            rep += "El momento de ejecución del trigger " + elem[0] + " en " + DBComparator.fstSchema + "."
                                + name + " es " + elem[1]+ " y en " + DBComparator.scdSchema + "." + name + " es " + elem2[1]+"\n"; 
                        }
                        if(!elem[2].equalsIgnoreCase(elem2[2])){
                            rep += "La condición del trigger " + elem[0] + " en " + DBComparator.fstSchema + "."
                                + name + " es " + elem[2]+ " y en " + DBComparator.scdSchema + "." + name + " es " + elem2[2]+"\n"; 
                        }
                    }
                    if(type.equalsIgnoreCase("index")){
                        if(!elem[1].equalsIgnoreCase(elem2[1])){
                            rep += "El indice " + elem[0] + " en " + DBComparator.fstSchema + "."
                                + name + " es " + elem[1]+ " y en " + DBComparator.scdSchema + "." + name + " es " + elem2[1]+"\n"; 
                        }
                        if(!elem[2].equalsIgnoreCase(elem2[2])){
                            rep += "El indice " + elem[0] + " en " + DBComparator.fstSchema + "."
                                + name + " esta definido sobre " + elem[2]+ " y en " + DBComparator.scdSchema + "." + name + " sobre " + elem2[2]+"\n"; 
                        }
                        if(!elem[3].equalsIgnoreCase(elem2[3])){
                            rep += "El indice " + elem[0] + " en " + DBComparator.fstSchema + "."
                                + name + " es " + elem[3]+ " y en " + DBComparator.scdSchema + "." + name + " es " + elem2[3]+"\n"; 
                        }
                    }
                    
                }
            }
            if(!find){
                if(type.equalsIgnoreCase("attribute")){
                    rep += "El atributo " + elem[0] +" de " + DBComparator.fstSchema + "." + name + " no se encuentra en " + DBComparator.scdSchema + "." + name+"\n";
                }
                if(type.equalsIgnoreCase("fk")){
                    rep += "La Foreign Key sobre " + elem[0] +" de " + DBComparator.fstSchema + "." + name + " no se encuentra en " + DBComparator.scdSchema + "." + name+"\n";
                }
                if(type.equalsIgnoreCase("trigger")){
                    rep += "El trigger " + elem[0] +" de " + DBComparator.fstSchema + "." + name + " no se encuentra en " + DBComparator.scdSchema + "." + name+"\n";
                }
                if(type.equalsIgnoreCase("index")){
                    rep += "El indice " + elem[0] +" de " + DBComparator.fstSchema + "." + name + " no se encuentra en " + DBComparator.scdSchema + "." + name+"\n";
                }
            }
                
        }
        for (int i = 0; i < scLA.size(); i++) {
            boolean find = false;
            String[] elem = scLA.get(i);
            for (int j = 0; j < fstLA.size(); j++) {
                String[] elem2 = fstLA.get(j);
                if(elem[0].equalsIgnoreCase(elem2[0])){
                    find=true;
                    break;
                }
            }
            if(!find){
                if(type.equalsIgnoreCase("attribute")){
                    rep += "El atributo " + elem[0] +" de " + DBComparator.scdSchema + "." + name + " no se encuentra en " + DBComparator.fstSchema + "." + name+"\n";
                }
                if(type.equalsIgnoreCase("fk")){
                    rep += "La Foreign Key sobre " + elem[0] +" de " + DBComparator.scdSchema + "." + name + " no se encuentra en " + DBComparator.fstSchema + "." + name+"\n";
                }
                if(type.equalsIgnoreCase("trigger")){
                    rep += "El trigger " + elem[0] +" de " + DBComparator.scdSchema + "." + name + " no se encuentra en " + DBComparator.fstSchema + "." + name+"\n";
                }
                if(type.equalsIgnoreCase("index")){
                    rep += "El indice " + elem[0] +" de " + DBComparator.scdSchema + "." + name + " no se encuentra en " + DBComparator.fstSchema + "." + name+"\n";
                }
            }
                
        }
        return rep;
    }
}

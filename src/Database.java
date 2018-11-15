/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordsecurity2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class Database {
    
    final static class MyResult {
        private final boolean first;
        private final String second;
        
        public MyResult(boolean first, String second) {
            this.first = first;
            this.second = second;
        }
        public boolean getFirst() {
            return first;
        }
        public String getSecond() {
            return second;
        }
    }
    
    protected static MyResult add(String fileName, String text) throws IOException{ 
        if(exist(fileName, text))
            return new MyResult(false, "Meno uz existuje");
        FileWriter fw = new FileWriter(fileName, true);
        fw.write(text + System.lineSeparator());
        fw.close();    
        return new MyResult(true, "");
    }
    
    protected static MyResult find(String fileName, String text) throws IOException{
        File frJm = new File(fileName);
        if (frJm.exists() == true){
            FileReader fr = new FileReader(frJm);
            BufferedReader bfr = new BufferedReader(fr);
            String riadok = "";
            String token = "";
            
            while ((riadok=bfr.readLine()) != null){
                StringTokenizer st = new StringTokenizer(riadok, ":");
                token = st.nextToken();
                if (token.equals(text)){
                    fr.close();
                    MyResult mr = new MyResult(true, riadok);
                    return mr;
                }
            }
            fr.close();   
            return new MyResult(false, "Meno sa nenaslo");              
        }
        return new MyResult(false, "Subor neexistuje");  
    }
    
    protected static boolean exist(String fileName, String text) throws IOException{
        StringTokenizer st = new StringTokenizer(text, ":");
        return find(fileName, st.nextToken()).getFirst();
    }
    
}

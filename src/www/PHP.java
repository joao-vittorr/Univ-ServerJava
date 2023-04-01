package www;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PHP {

    public static String exec(String file, String[] parametros){
        try{

            String cmd = "C:\\xampp\\php\\php.exe" + file + " " + String.join(" ", parametros);
            System.out.println(cmd);
            Process processo = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(processo.getInputStream()));

            String ret = "";
            String linha;
            while((linha = br.readLine()) != null) {
                ret += linha;
            }
            return ret;
        }catch(Exception e){
            e.printStackTrace();
            return "!Erro ao interpretar o arquivo php:"+ file;
        }
    }
}

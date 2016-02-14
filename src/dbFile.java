
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aadi
 */
public class dbFile {
    static String people = "people.txt";
    static String friend = "friend.txt";
    static boolean findFiles(){
        try{
            FileInputStream in = new FileInputStream(people);
            in = new FileInputStream(friend);
            in.close();
            in.close();
        } catch (FileNotFoundException e){
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    
}

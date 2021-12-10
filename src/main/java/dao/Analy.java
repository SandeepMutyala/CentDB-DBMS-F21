package dao;


import com.user_interface.UserInterface;
import utils.GlobalSessionDetails;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Analy {

    static Scanner sc = new Scanner(System.in);
    static File q = new File("queries.txt");
    static File u = new File("updates.txt");
    static File d = new File("deletes.txt");


    public static void analysis() throws IOException {
        System.out.println("Enter your Query!");
        String queryString = sc.nextLine().toLowerCase();
        String[] qS = queryString.split(" ");

        switch (qS[1]) {
            case "queries":
                queries(qS);
                break;

            case "update":
                update(qS);
                break;
            case "delete":
                deletes(qS);
                break;

            default:
                System.out.println("Error - you will be redirected");
                UserInterface.userView();

        }


    }

    private static void update(String[] qS) throws FileNotFoundException {
        String db = qS[2];
        int x=0;
        ArrayList<String> qq = new ArrayList<>();
        if (u.exists()) {
            Scanner scanner = new Scanner(u);
            while (scanner.hasNextLine()) {
                qq.add(scanner.nextLine());
            }
            for (String d : qq) {
                if (d.contains(db)) {
                    String[] dd = d.split(":");
                    System.out.println("Total " + dd[2] + " Update operations are performed on " + dd[1]);
                    x = 1;
                }
            }
            if (x == 0) {
                System.out.println("please input your query again");
            }
        }
        else{
            System.out.println("no analysis recorded on this query");
        }
    }
    private static void deletes(String[] qS) throws FileNotFoundException {
        String db = qS[2];
        int x=0;
        ArrayList<String> qq = new ArrayList<>();

        if (d.exists()) {
            Scanner scanner = new Scanner(d);
            while (scanner.hasNextLine()) {
                qq.add(scanner.nextLine());
            }
            for (String d : qq) {
                if (d.contains(db)) {
                    String[] dd = d.split(":");
                    System.out.println("Total " + dd[2] + " Delete operations are performed on " + dd[1]);
                    x = 1;
                }
            }
            if (x==0){
                System.out.println("please input your query again");
            }
        }
        else{
            System.out.println("no analysis recorded on this query");

        }

    }

    private static void queries(String[] qS) throws FileNotFoundException {
        String db = qS[2];
        int x=0;
        ArrayList<String> qq = new ArrayList<>();

        if (q.exists()) {
            Scanner scanner = new Scanner(q);
            while (scanner.hasNextLine()) {
                qq.add(scanner.nextLine());
            }
            for (String d : qq) {
                if (d.contains(db) && d.contains(GlobalSessionDetails.loggedInUsername)) {
                    String[] dd = d.split(":");
                    System.out.println("user " + dd[1] + " submitted " + dd[2] + " queries on " + dd[0]);
                    x = 1;
                    break;
                }
            }
            if (x == 0) {
                System.out.println("please input your query again");
            }
        }
        else {
            System.out.println("no analysis recorded on this query");
        }

    }



    public static void query(String Db, int count, String user) throws IOException {
        ArrayList<String> qs = new ArrayList<>();
        ArrayList<String> qss = new ArrayList<>();
        String[] qa = new String[3];
        qa[0] = Db;
        qa[1] = user;
        qa[2] = String.valueOf(count);
        String newcont = "";
        int x = 0;
        int c;

        String e1 = qa[0] + ":" + qa[1] + ":" + qa[2];
        if (q.exists()) {
            Scanner so = new Scanner(q);
            while (so.hasNextLine()) {
                qs.add(so.nextLine());
            }
            for (String data : qs) {
                if(!(Db ==null) && !(user ==null) ) {

                    if (data.contains(Db) && data.contains(user)) {
                        String[] d = data.split(":");
                        c = Integer.parseInt(d[2]);
                        c += 1;
                        newcont = d[0] + ":" + d[1] + ":" + c;
                        x = 1;
                    }
                }else {
                    newcont = data;
                }

                qss.add(newcont);

            }
            if (x ==0 && !(Db ==null) && !(user ==null) ){
                qss.add(e1);
            }
            FileWriter nn = new FileWriter(q);
            for (String d : qss) {
                nn.write(d);
                nn.write((System.getProperty("line.separator")));
            }
            nn.close();
        } else {
            try {
                if (qa[0] != null) {


                    q.createNewFile();
                    FileWriter mw = new FileWriter(q);
                    mw.write(e1);
                    mw.close();
                }
            } catch(IOException e){
                e.printStackTrace();
            }


        }


    }


    public static void update(String Db, int count, String table) throws IOException {
        ArrayList<String> qs = new ArrayList<>();
        ArrayList<String> qss = new ArrayList<>();
        String[] qa = new String[3];
        qa[0] = Db;
        qa[1] = table;
        qa[2] = String.valueOf(count);
        String newcont = "";
        int x= 0;

        int c;
        String e1 = qa[0] + ":" + qa[1] + ":" + qa[2];
        if (u.exists()) {
            Scanner so = new Scanner(u);
            while (so.hasNextLine()) {
                qs.add(so.nextLine());
            }
            for (String data : qs) {
                if(!(Db ==null) && !(table ==null) ) {
                    if (data.contains(Db) && data.contains(table)) {
                        String[] d = data.split(":");
                        c = Integer.parseInt(d[2]);
                        c += 1;
                        x = 1;
                        newcont = d[0] + ":" + d[1] + ":" + c;
                    } else {
                        newcont = data;
                    }
                }
                qss.add(newcont);

            }
            if (x ==0 && !(Db ==null) && !(table ==null) ){
                qss.add(e1);
            }
            FileWriter nn = new FileWriter(u);
            for (String d : qss) {
                nn.write(d);
                nn.write((System.getProperty("line.separator")));
            }
            nn.close();
        } else {
            try {
                u.createNewFile();
                FileWriter mw = new FileWriter(u);
                mw.write(e1);
                mw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public static void delete(String Db, int count, String table) throws IOException {
        ArrayList<String> qs = new ArrayList<>();
        ArrayList<String> qss = new ArrayList<>();
        String[] qa = new String[3];
        qa[0] = Db;
        qa[1] = table;
        qa[2] = String.valueOf(count);
        String newcont = "";
        int x= 0;

        int c;
        String e1 = qa[0] + ":" + qa[1] + ":" + qa[2];
        if (d.exists()) {
            Scanner so = new Scanner(d);
            while (so.hasNextLine()) {
                qs.add(so.nextLine());
            }
            for (String data : qs) {
                if(!(Db ==null) && !(table ==null) ) {
                    if (data.contains(Db) && data.contains(table)) {
                        String[] d = data.split(":");
                        c = Integer.parseInt(d[2]);
                        c += 1;
                        x = 1;
                        newcont = d[0] + ":" + d[1] + ":" + c;
                    } else {
                        newcont = data;
                    }
                }
                qss.add(newcont);

            }
            if (x ==0 && !(Db ==null) && !(table ==null) ){
                qss.add(e1);
            }
            FileWriter nn = new FileWriter(d);
            for (String d : qss) {
                nn.write(d);
                nn.write((System.getProperty("line.separator")));
            }
            nn.close();
        } else {
            try {
                d.createNewFile();
                FileWriter mw = new FileWriter(u);
                mw.write(e1);
                mw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}

import net.sf.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;

/**
 * Created with IDEA
 * author:xuzhaohui
 * Date:18-5-15
 * Time:下午2:20
 */
public class science {

    public static String baseUrl = "http://www.baike.com/category/Ajax_cate.jsp?catename=";
    public static String all = "自然\t文化\t人物\t历史\t生活\t社会\t艺术\t经济\t体育\t技术\t地理";
    //写文件
    public static BufferedWriter out=null;

    static {
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("science1.txt", true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException {

        System.out.println("任务开始!");

        //String[] str = {"自然","文化","人物","历史","生活","社会","艺术","经济","科学","体育","技术","地理"};

        String name ="科学";

        String url = baseUrl+name;

        String json = getJSON(url);

        JSONArray ja = JSONArray.fromObject(json.trim());

        dataProcess(ja,name);


        //关闭文件
        fileClose();

        System.out.println("任务结束!");
    }

    //自然json    自然  info为写入的内容
    public static void dataProcess(JSONArray jsonArray,String info) throws IOException {

        //List<Info> list = new ArrayList<Info>();
        //String temp = null;
        for (int i = 0; i < jsonArray.size(); i++) {

            String name = jsonArray.getJSONObject(i).getString("name");

            String body = getJSON(baseUrl + name);
            //用得到name判断是否还能调用
            //String name_temp = ja.getJSONObject(0).getString("name");



            //原有的信息重复,直接跳过
            if (info.contains(name)) {
                System.out.println(info + "\t" + name + "重复");
                continue;
            }

            //如果有大类,避免去循环,不再递归
            if(all.contains(name)){
                //写入文件
                writeFile(info + "\t" + name);

                System.out.println(info + "\t" + name);
                //继续下次循环
                continue;
            }

            //不能再调用  返回值为testing
            if (body.trim().equals("testing")) {
                //写入文件
                writeFile(info + "\t" + name);

                System.out.println(info + "\t" + name);
                //继续下次循环
                continue;
            }

            //不能再调用  JsonArray为[]
            JSONArray ja = JSONArray.fromObject(body);

             if (ja.size() == 0) {
                    //写入文件
                    writeFile(info + "\t" + name);

                    System.out.println(info + "\t" + name);

                    //继续下次循环
                    continue;

             }
                //如果还有下次调用,需要加上原先的info
              dataProcess(ja, info + "\t" + name);

        }

    }

    //根据URL返回json
    public static String getJSON(String url) throws IOException {

        Connection.Response res = Jsoup.connect(url)
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                .timeout(300000).ignoreContentType(true).execute();//.get();
        String body = res.body();


        return body;
    }



    //按行写入文件
    public static void writeFile(String str) throws IOException {


        out.write(str + "\r\n");



    }

    public static void fileClose() throws IOException {
        out.close();
    }



}

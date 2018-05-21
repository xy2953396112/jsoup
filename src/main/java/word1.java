import net.sf.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Iterator;

/**
 * Created with IDEA
 * author:xuzhaohui
 * Date:18-5-15
 * Time:下午2:20
 */
public class word1 {

    public static String baseUrl = "http://www.baike.com/category/Ajax_cate.jsp?catename=";
    //public static String all = "自然\t文化\t人物\t历史\t生活\t社会\t艺术\t经济\t体育\t技术\t地理";
    //list请求访问url
    public static String listWord = "http://fenlei.baike.com/word/list/";
    //写文件
    public static BufferedWriter out=null;

    static {
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("word5.txt", true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException {

        System.out.println("任务开始!");

        //String[] str = {"自然","文化","人物","历史","生活","社会","艺术","经济","科学","体育","技术","地理"};

        String name ="饮食";

        String url = baseUrl+name;
        //饮食的json
        String json = getJSON(url);

        JSONArray ja = JSONArray.fromObject(json.trim());
        //ja为子的目录列表
        dataProcess(ja,name);

        //关闭文件
        fileClose();

        System.out.println("任务结束!");
    }

    //自然json    自然  info为写入的内容    jsonArray为关键词的子目录信息  info为关键词
    public static void dataProcess(JSONArray jsonArray,String info) throws IOException {

        //获取目录的信息
        //String temp = getJSON(listWord.replace("word",name));
        //扒龙须菜	饮食/烹饪/中国烹饪/八大菜系/川菜 能够解析出川菜    饮食能够解析出饮食
        String str = info.substring(info.lastIndexOf("/")+1);
        String content_info = getJSON(listWord.replace("word",str));
        if(!str.equals("饮食"))
            wordMark(content_info,info);
        else
            wordMark(content_info,str);
        //List<Info> list = new ArrayList<Info>();
        //String temp = null;
        for (int i = 0; i < jsonArray.size();i++) {
            //得到JsonArray的返回的第一个子数组元素的名称
            String name = jsonArray.getJSONObject(i).getString("name");
            //返回子Json的接口
            String body = getJSON(baseUrl + name);
            //页面解析的内容
            String content = getJSON(listWord.replace("word",name));
            //原有的信息重复,直接跳过
            if (info.contains(name)) {
                System.out.println(info + "\t" + name + "重复");
                continue;
            }
            //不能再调用  返回值为testing
            if (body.trim().equals("testing")) {
                //写入文件  body为网页内容   info为目录
                wordMark(content,info+"/"+name);

                //System.out.println(info + "/" + name);
                //继续下次循环
                continue;
            }
            //不能再调用  JsonArray为[]
            JSONArray ja = JSONArray.fromObject(body);
            //size=0不能继续循环
            if (ja.size() == 0) {
                 //写入文件  body为网页内容   info为目录
                 wordMark(content,info+"/"+name);
                 //控制台打印
                 System.out.println(info + "/1" + name);
                 //继续下次循环
                 continue;
                 }
            //如果还有下次调用,需要加上原先的info
            dataProcess(ja, info + "/2" + name);

        }

    }

    //根据URL返回json       返回页面的时候会有延迟
    public static String getJSON(String url) throws IOException {
        //在请求页面时,会有延迟
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



    //按行写入文件  每写入一行换行
    public static void writeFile(String str) throws IOException {

        out.write(str + "\r\n");

    }

    //文件关闭
    public static void fileClose() throws IOException {
        out.close();
    }

    //content为网页具体内容,info为目录
    public static void wordMark(String content,String info) throws IOException {

        Document doc = Jsoup.parse(content);

        Elements a = doc.getElementsByTag("dd");

        for(Element ele : a){
            //得到<a>标签的内容
            String text = ele.getElementsByTag("a").text();
            //写入文件
            writeFile(text+"\t"+info);
            System.out.println(text+"\t3"+info);
        }

    }



}
/**
 * FileName: PmsUploadUtil
 * <p>
 * Author: mac
 * <p>
 * Date: 2020/4/19 3:24 下午
 * <p>
 * Description:
 * <p>
 * History:
 *
 * <author> <time> <version> <desc>
 * <p>
 * 作者姓名 修改时间 版本号 描述
 */
package com.atguigu.gmall.manage.util;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图片上传处理
 * @author mac

 * @create 2020/4/19
 *

 */
public class PmsUploadUtil {

    public static String uploadImage(MultipartFile multipartFile) {
        String imgUrl = "http://172.16.231.171";

        //上传图片到服务器
        //配置fdfs的全局连接地址
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();//获取文件的路径

        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();
        //获取一个trackerServer实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过trackerServer获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer, null);
        try {
            byte[] bytes = multipartFile.getBytes();//获得上传的二进制对象
            String originalFilename = multipartFile.getOriginalFilename();//a.jpg
            //获取后缀名
            String extName = StringUtils.substringAfterLast(originalFilename, ".");
            String[] uploadInfos = storageClient.upload_file(bytes, extName, null);

            for (String uploadInfo : uploadInfos){
                imgUrl += "/" + uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgUrl;
    }
}

package com.jan.web.fileservice;

import com.fasterxml.jackson.core.type.TypeReference;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ContainerFileService implements FileService
{
    @Override
    public List<FileInformation> getAllFiles()
    {
        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();
        try
        {
            URL url = new URL("http://localhost:9999/getfiles");
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())))
            {
                for (String line; (line = reader.readLine()) != null; )
                {
                    result.append(line);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        } catch (ProtocolException e)
        {
            e.printStackTrace();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        List<FileInformation> fileInformationList = new ArrayList<>();
        try
        {
            FileResponse response = mapper.readValue(result.toString(), FileResponse.class);
            response.directories.forEach(directory -> fileInformationList.add(new FileInformation(directory)));
            response.files.forEach(file -> fileInformationList.add(new FileInformation(file.key, file.size, (long)file.modified)));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return fileInformationList;
    }
}

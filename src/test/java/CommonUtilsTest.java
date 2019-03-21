import com.project.test.commons.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, URLConnection.class, CommonUtils.class, Files.class})
public class CommonUtilsTest {

    private boolean isDirAvailable = false;

    @Before
    public void setUp() {
        try {
            Files.createDirectory(Paths.get("tempDir"));
            isDirAvailable = true;
        } catch ( IOException e) {
            isDirAvailable = false;
        }
    }


    @After
    public void cleanUp() {
        try {

            Files.walk(Paths.get("tempDir"))
                    .map(Path::toFile)
                    .forEach(File::delete);

            Files.deleteIfExists(Paths.get("tempDir"));
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUrlConnectionTest() throws Exception{
        URL url = PowerMockito.mock(URL.class);
        URLConnection urlConnection = PowerMockito.mock(URLConnection.class);
        PowerMockito.when(url.openConnection()).thenReturn(urlConnection);
        PowerMockito.doNothing().when(urlConnection).setConnectTimeout(Mockito.anyInt());
        PowerMockito.doNothing().when(urlConnection).setReadTimeout(Mockito.anyInt());
        URLConnection urlConnection1 = CommonUtils.getUrlConnection(url, 1000, 1000);
        Assert.assertEquals(urlConnection, urlConnection1);
    }

    @Test(expected =  IOException.class)
    public void getUrlConnectionTestForIOException() throws  IOException{
        URL url = PowerMockito.mock(URL.class);
        URLConnection urlConnection = PowerMockito.mock(URLConnection.class);
        PowerMockito.when(url.openConnection()).thenThrow(IOException.class);
        URLConnection urlConnection1 = CommonUtils.getUrlConnection(url, 1000, 1000);
    }

    @Test
    public void moveFileFromTmpWhenFileExistsTest() throws  IOException {
        Files.createFile(Paths.get("/tmp/123"));
        Files.createFile(Paths.get("tempDir/123"));
        CommonUtils.moveFileFromTmp("tempDir", "123");
        long count = Files.walk(Paths.get("tempDir")).count();
        Assert.assertEquals(2, count-1);
    }

    @Test
    public void moveFileFromTmpWhenFileDoesNotExistTest() throws IOException {
        Files.createFile(Paths.get("/tmp/123"));
        CommonUtils.moveFileFromTmp("tempDir", "123");
        long count = Files.walk(Paths.get("tempDir")).count();
        Assert.assertEquals(1, count-1);
    }

    @Test
    public void writeToFileTest() throws IOException {
        BufferedInputStream bufferedInputStream = PowerMockito.mock(BufferedInputStream.class);
        FileOutputStream fileOutputStream = PowerMockito.mock(FileOutputStream.class);
        String data = "Hello World";
        PowerMockito.when(bufferedInputStream.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt())).
                                        then(new Answer() {
                                            int count = 0;
                                            public Object answer(InvocationOnMock invocationOnMock){
                                                if(count == 0) {
                                                    count++;
                                                    return data.length();
                                                }
                                                return -1;
                                            }
                                        });

        PowerMockito.doNothing().when(fileOutputStream).write(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
        long count = CommonUtils.writeToFile(bufferedInputStream, fileOutputStream);
        Assert.assertEquals(data.length(), count);
    }

    @Test
    public void readFileTest() throws Exception {
        String data = "Hello World";
        URLConnection urlConnection = PowerMockito.mock(URLConnection.class);
        PowerMockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(data.getBytes()));
        FileOutputStream fileOutputStream = new FileOutputStream("/tmp/xyz");
        BufferedInputStream bufferedInputStream = PowerMockito.mock(BufferedInputStream.class);
        PowerMockito.when(bufferedInputStream.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt())).
                then(new Answer() {
                    int count = 0;
                    public Object answer(InvocationOnMock invocationOnMock) {
                        if(count < 2) {
                            count++;
                            return data.length();
                        }
                        return -1;
                    }
                });
        long readSize = CommonUtils.readFile(bufferedInputStream, fileOutputStream, "xyz");
        Files.deleteIfExists(Paths.get("/tmp/xyz"));
        Assert.assertEquals(data.length()*2, readSize);
    }

    @Test
    public void readFileAndFailDownloadTest() throws Exception {
        String data = "Hello World";
        URLConnection urlConnection = PowerMockito.mock(URLConnection.class);
        PowerMockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(data.getBytes()));
        FileOutputStream fileOutputStream = new FileOutputStream("/tmp/xyz");
        BufferedInputStream bufferedInputStream = PowerMockito.mock(BufferedInputStream.class);
        PowerMockito.when(bufferedInputStream.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt())).
                then(new Answer() {
                    int count = 0;
                    public Object answer(InvocationOnMock invocationOnMock) throws SocketTimeoutException {
                        if(count == 0) {
                            count++;
                            return data.length();
                        }
                        throw new SocketTimeoutException();
                    }
                });
        long readSize = CommonUtils.readFile(bufferedInputStream, fileOutputStream, "xyz");
        boolean result1 = Files.exists(Paths.get("tempDir/xyz"));
        boolean result2 = Files.exists(Paths.get("/tmp/xyz"));
        Assert.assertEquals(false, result1);
        Assert.assertEquals(false, result2);
        Assert.assertEquals(-1, readSize);
    }
}

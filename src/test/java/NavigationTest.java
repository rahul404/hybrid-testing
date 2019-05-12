import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class NavigationTest {

    public ArrayList<ArrayList<String>> readData(XSSFSheet xssfSheet){
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        Iterator<Row> rowIterator = xssfSheet.rowIterator();
        while (rowIterator.hasNext()){
            ArrayList<String> list = new ArrayList<String>();
            Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
            while (cellIterator.hasNext()){
                String val = cellIterator.next().getStringCellValue();
                System.out.print(val);
                list.add(val);
            }
            System.out.println();
            data.add(list);
        }
        return data;
    }
    @Test
    public void navigate() throws Exception{
        WebDriver webDriver = new ChromeDriver();
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(new File("hybrid.xlsx")));
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = xssfSheet.rowIterator();
        ArrayList<ArrayList<String>> data = readData(xssfWorkbook.getSheetAt((int)rowIterator.next().cellIterator().next().getNumericCellValue()));
        for(int i = 0; i< data.size(); i++){
            rowIterator = xssfSheet.rowIterator();
            rowIterator.next(); // skip first
            int j = 0;
            while (rowIterator.hasNext()){
                Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
                String command = cellIterator.next().getStringCellValue().toLowerCase().trim();
                System.out.println("command = "+command);
                if (command.equals("goto")){
                    String url = data.get(i).get(j);
                    System.out.println("url = "+url);
                    webDriver.get(url);
                    Assert.assertEquals(webDriver.getCurrentUrl(), url);
                }
                else if (command.equals("type")){
                    String findBy = cellIterator.next().getStringCellValue().toLowerCase();
                    if (findBy.equals("name")){
                        webDriver.findElement(By.name(cellIterator.next().getStringCellValue())).sendKeys(data.get(i).get(j));
                    }
                    if (findBy.equals("id")){
                        webDriver.findElement(By.id(cellIterator.next().getStringCellValue())).sendKeys(data.get(i).get(j));
                    }
                }
                else if (command.equals("click")){
                    String findBy = cellIterator.next().getStringCellValue().toLowerCase();
                    if (findBy.equals("name")){
                        webDriver.findElement(By.name(cellIterator.next().getStringCellValue())).click();
                        Assert.assertEquals(webDriver.getCurrentUrl(), data.get(i).get(j));
                    }
                    if (findBy.equals("id")){
                        webDriver.findElement(By.id(cellIterator.next().getStringCellValue())).click();
                        Assert.assertEquals(webDriver.getCurrentUrl(), data.get(i).get(j));
                    }
                }
                j++;
            }
        }
    }
}

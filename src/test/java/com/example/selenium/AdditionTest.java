package com.example;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdditionTest {

    @Test
    public void testAdditionRandom() throws IOException {
        // Define report file path (src/test/resources/addition_test_report.txt)
        Path reportPath = Paths.get("src", "test", "resources", "addition_test_report.txt");
        
        // Create parent directories if they don't exist
        Files.createDirectories(reportPath.getParent());
        
        // Initialize report file with header
        try (BufferedWriter writer = Files.newBufferedWriter(reportPath, 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("Addition Test Report\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
            writer.write("========================================\n\n");
        }

        WebDriverManager.chromedriver()
                .driverVersion("145.0.7632.117")
                .setup();

        WebDriver driver = new ChromeDriver();

        try {
            driver.get("file:///S:/addition-webapp/src/main/webapp/index.html");

            WebElement num1Field = driver.findElement(By.id("num1"));
            WebElement num2Field = driver.findElement(By.id("num2"));
            WebElement addButton = driver.findElement(By.tagName("button"));
            WebElement resultElement = driver.findElement(By.id("result"));

            java.util.Random rand = new java.util.Random();
            System.out.println("Starting random addition tests...\n");
            
            // Append to report
            appendToReport(reportPath, "Starting random addition tests...\n");

            for (int i = 0; i < 10; i++) {
                double a = rand.nextInt(200) - 100;
                double b = rand.nextInt(200) - 100;
                double expected = a + b;

                String logMessage = String.format("Test %d: %.2f + %.2f = %.2f (expected) ", i+1, a, b, expected);
                System.out.print(logMessage);

                num1Field.clear();
                num2Field.clear();
                num1Field.sendKeys(String.valueOf(a));
                num2Field.sendKeys(String.valueOf(b));
                addButton.click();

                String resultText = resultElement.getText();
                double actual = Double.parseDouble(resultText.replace("Result: ", "").trim());

                String status = (Math.abs(actual - expected) < 0.0001) ? "✓" : "✗";
                String resultLine = String.format("→ actual: %.2f %s%n", actual, status);
                System.out.print(resultLine);

                // Write to report
                appendToReport(reportPath, logMessage + resultLine);

                assertEquals(expected, actual, 0.0001, "Failed for " + a + " + " + b);
            }

            String successMsg = "\nAll tests passed successfully!\n";
            System.out.println(successMsg);
            appendToReport(reportPath, successMsg);

        } finally {
            driver.quit();
        }
    }

    private void appendToReport(Path path, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(content);
        }
    }
}
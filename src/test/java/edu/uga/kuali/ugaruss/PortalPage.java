/*
 *  Copyright (c) 2013, Leo Przybylski
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met: 
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer. 
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution. 
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies, 
 *  either expressed or implied, of the FreeBSD Project.
 */
package edu.uga.kuali.ugaruss;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Leo Przybylski
 */
public class PortalPage extends Page {

    public PortalPage(WebDriver driver) {
        super(driver);
    }

    public OffCampusEquipmentRequestDocumentPage clickOffCampusEquipmentRequest() {
        final WebElement link = driver.findElement(By.linkText("Off-Campus Equipment Request"));
        link.click();

        driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(0));
        
        // System.out.println("Page is " + driver.getPageSource());

        driver.switchTo().frame("iframeportlet");

        // System.out.println("Page is " + driver.getPageSource());

        WebDriverWait wait = new WebDriverWait(driver, 40);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("document.newMaintainableObject.dataObject.inventoryNumber")));        

        return new OffCampusEquipmentRequestDocumentPage(driver);
    }

    public OffCampusEquipmentRequestSearchPage clickOffCampusEquipmentRequestSearch() {
        final WebElement button = driver.findElement(By.linkText("Document Search: Off-Campus Equipment Request/Renew"));
        button.click();
        return new OffCampusEquipmentRequestSearchPage(driver);
    }

    public PortalPage clickMainMenu() {
        driver.switchTo().defaultContent();

        final WebElement button = driver.findElement(By.linkText("Main Menu"));
        button.click();
        return this;
    }
}
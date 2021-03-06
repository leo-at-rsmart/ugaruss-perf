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
        waitFor(By.linkText("Off-Campus Equipment Request"));

        driver.findElement(By.linkText("Off-Campus Equipment Request")).click();

        driver.switchTo().defaultContent();
        switchToIFramePortlet();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("document.newMaintainableObject.dataObject.inventoryNumber")));

        return new OffCampusEquipmentRequestDocumentPage(driver);
    }

    public OffCampusEquipmentRequestSearchPage clickOffCampusEquipmentRequestSearch() {
        waitFor(By.linkText("Off-Campus Equipment Request"));
        driver.findElement(By.linkText("Document Search: Off-Campus Equipment Request/Renew")).click();

        driver.switchTo().defaultContent();
        switchToIFramePortlet();

        return new OffCampusEquipmentRequestSearchPage(driver);
    }

    protected OffCampusEquipmentRequestDocumentPage searchFor(final String documentNumber) {
        final String documentNumberXpath = "//a[text()[contains(., '" + documentNumber + "')]]";

        clickMainMenu().clickOffCampusEquipmentRequestSearch();

        driver.switchTo().defaultContent();
        switchToIFramePortlet();
        waitFor(By.name("methodToCall.search"));

        getElementByName("rangeLowerBoundKeyPrefix_dateCreated", true).sendKeys("07/01/2013");
        
        getElementByName("methodToCall.search", true).click();
        
        waitFor(By.xpath(documentNumberXpath));

        driver.findElement(By.xpath(documentNumberXpath)).click();

        for (final String winHandle : driver.getWindowHandles()) {
            System.out.println("Switching... to " + winHandle);
            driver.switchTo().window(winHandle);
        }
            
        return new OffCampusEquipmentRequestDocumentPage(driver);
    }
}

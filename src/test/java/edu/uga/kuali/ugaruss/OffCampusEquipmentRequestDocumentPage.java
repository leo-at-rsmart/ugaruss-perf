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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Leo Przybylski
 */
public class OffCampusEquipmentRequestDocumentPage extends Page {

    public OffCampusEquipmentRequestDocumentPage(WebDriver driver) {
        super(driver);
    }

    public OffCampusEquipmentRequestDocumentPage setInventoryNumber(final String inventoryNumber) {
        final WebElement field = driver.findElement(By.name("document.newMaintainableObject.dataObject.inventoryNumber"));
        field.sendKeys(inventoryNumber);
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage setInventoryDescription(final String description) {
        final WebElement field = driver.findElement(By.name("document.newMaintainableObject.dataObject.equipmentInventory.inventoryDescription"));
        field.sendKeys(description);
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage setInventoryDepartment(final String department) {
        final WebElement field = driver.findElement(By.name("document.newMaintainableObject.dataObject.equipmentInventory.department"));
        field.sendKeys(department);
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage setRequestedDate(final String date) {
        final WebElement field = driver.findElement(By.name("document.newMaintainableObject.dataObject.requestedDate"));
        field.sendKeys(date);
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage setExpireDate(final String date) {
        final WebElement field = driver.findElement(By.name("document.newMaintainableObject.dataObject.expireDate"));
        field.sendKeys(date);
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage setPurpose(final String purpose) {
        final WebElement field = driver.findElement(By.name("document.newMaintainableObject.dataObject.purpose"));
        field.sendKeys(purpose);
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage setSensitive(final Boolean isSensitive) {
        final List<WebElement> fields = driver.findElements(By.name("document.newMaintainableObject.dataObject.sensitiveData"));
        for (final WebElement field : fields) {
            if ("yes".equals(field.getAttribute("value")) && isSensitive) {
                field.click();
                return this;
            }
            else if ("no".equals(field.getAttribute("value")) && !isSensitive) {
                field.click();
                return this;
            }
        }
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage submit() {
        final WebElement button = driver.findElement(By.xpath("//button[text()[contains(., 'submit')]]"));
        button.click();
        return this;
    }

    public OffCampusEquipmentRequestDocumentPage approve() {
        final String expression = "//button[text()[contains(., 'approve')]]";
        waitFor(By.xpath(expression));
        driver.findElement(By.xpath(expression)).click();
        return this;
    }


    public PortalPage clickMainMenu() {
        final WebElement button = driver.findElement(By.linkText("Main Menu"));
        button.click();
        return new PortalPage(driver);
    }

    public String getDocumentNumber() {
        final String expression = "//*[@data-label='Document Number']/span[1][normalize-space()]";
        waitFor(By.xpath(expression));
        return driver.findElement(By.xpath(expression)).getText().trim();
    }
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.SystemClock;

import com.google.common.base.Function;

/**
 *
 * @author Leo Przybylski
 */
public abstract class Page {
    protected final WebDriver driver;
    protected Wait<WebDriver> wait;

    protected Page(final WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }


    protected void waitForFormLoad() {
        new ElementExistsWaiter("Page did not load").until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    boolean isFormComplete = false;
                    
                    List<WebElement> elements = driver.findElements(By.id("formComplete"));
                    if (!elements.isEmpty()) {
                        isFormComplete = true;
                    }
                    
                    return isFormComplete;
                }
            }
            );
    }


    protected boolean switchToIFramePortlet() {
        boolean switchToIFramePortletSuccessful = true;
        
        try {            
            driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(0));
            driver.switchTo().frame("iframeportlet");
        } catch (Exception e) {
            switchToIFramePortletSuccessful = false;
        }
        
        return switchToIFramePortletSuccessful;
    }

    protected WebElement getElementByName(final String name, final boolean exact) {
        WebElement element = null;
        
        List<WebElement> elements = getElementsByName(name, exact);
        if (!elements.isEmpty()) {
            element = elements.get(0);
        }
        
        return element;
    }

    protected List<WebElement> getElementsByName(final String name, final boolean exact) {
        driver.switchTo().defaultContent();
        
        List<WebElement> elements = getActiveElementsByName(name, exact);
        
        if (elements.isEmpty()) {
            if (switchToIFramePortlet()) {
                elements.addAll(getActiveElementsByName(name, exact));
            }
        }
        
        return elements;
    }

    protected List<WebElement> getActiveElementsByName(final String name, final boolean exact) {
        List<WebElement> activeElements = new ArrayList<WebElement>();
        
        List<WebElement> elements = new ArrayList<WebElement>();
        if (exact) {
            elements.addAll(driver.findElements(By.name(name)));
        } else {
            elements.addAll(driver.findElements(By.xpath(getAttributeContainsXPath("name", name))));
        }

        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                activeElements.add(element);
            }
        }
        
        return activeElements;
    }


    protected String getAttributeContainsXPath(final String attribute, final String text) {
        return "//*[contains(@" + attribute + ", '" + text + "')]";
    }


    /**
     * Implements a {@code Wait<WebDriver>} class for waiting for elements (especially Ajax elements) to appear on the page within a specified timeout.  
     * Modified from {@code WebDriverWait} in order to integrate custom JUnit4 assertion messages. 
     * 
     * @see org.openqa.selenium.support.ui.Wait
     * @see org.openqa.selenium.support.ui.WebDriverWait
     */
    class ElementExistsWaiter implements Wait<WebDriver> {
        
        private final Clock clock = new SystemClock();
        private final long testTimeOut = 10000;
        private final long sleepTimeOut = 500;
        
        private String message;
        
        protected ElementExistsWaiter(String message) {
            this.message = message;
        }
        
        /**
         * {@inheritDoc}
         * @see org.openqa.selenium.support.ui.Wait#until(com.google.common.base.Function)
         */
        public <T> T until(Function<? super WebDriver, T> exists) {
            long end = clock.laterBy(testTimeOut);
            while (clock.isNowBefore(end)) {
                T value = exists.apply(driver);
                
                if (value != null) {
                    if (Boolean.class.equals(value.getClass())) {
                        if (Boolean.TRUE.equals(value)) {
                            return value;
                        }
                    } else {
                        return value;
                    }
                }
                
                sleep();
            }
            
            throw new AssertionError(message);
        }
        
        private void sleep() {
            try {
                Thread.sleep(sleepTimeOut);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Implements a {@code Wait<WebDriver>} class for waiting for elements (especially Ajax elements) to not appear on the page within a specified timeout.  
     * Modified from {@code WebDriverWait} in order to integrate custom JUnit4 assertion messages and add the not condition. 
     * 
     * @see org.openqa.selenium.support.ui.Wait
     * @see org.openqa.selenium.support.ui.WebDriverWait
     */
    private class ElementDoesNotExistWaiter implements Wait<WebDriver> {
        
        private final Clock clock = new SystemClock();
        private final long testTimeOut = 1000;
        private final long sleepTimeOut = 500;
        
        private String message;
        
        protected ElementDoesNotExistWaiter(String message) {
            this.message = message;
        }
        
        /**
         * {@inheritDoc}
         * @see org.openqa.selenium.support.ui.Wait#until(com.google.common.base.Function)
         */
        public <T> T until(Function<? super WebDriver, T> exists) {
            long end = clock.laterBy(testTimeOut);
            while (clock.isNowBefore(end)) {
                T value = exists.apply(driver);
                
                if (value != null) {
                    if (Boolean.class.equals(value.getClass())) {
                        if (Boolean.TRUE.equals(value)) {
                            throw new AssertionError(message);
                        }
                    } else {
                        throw new AssertionError(message);
                    }
                }
                
                sleep();
            }
            
            return null;
        }
        
        private void sleep() {
            try {
                Thread.sleep(sleepTimeOut);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Mimics an {@code ElementExistsWaiter} to determine whether an element exists on the page or not.
     *
     * @see org.openqa.selenium.support.ui.WebDriverWait
     */
    private class ElementExistenceFinderWaiter {
        
        private final Clock clock = new SystemClock();
        private final long testTimeOut = 1000;
        private final long sleepTimeOut = 500;
        
        /**
         * Mimics the {@code ElementExistsWaiter.until(..)} method and waits until the function evaluates to a value that is non-null. If the function becomes
         * non-null within a certain time, then this method will return true, otherwise, it will return false.
         *
         * @param exists the function to evaluate
         * 
         * @see org.openqa.selenium.support.ui.Wait#until(com.google.common.base.Function)
         */
        public <T> boolean until(Function<WebDriver, T> exists) {
            long end = clock.laterBy(testTimeOut);
            while (clock.isNowBefore(end)) {
                T value = exists.apply(driver);
                
                if (value != null) {
                    return true;
                }
                
                sleep();
            }
            
            return false;
        }
        
        private void sleep() {
            try {
                Thread.sleep(sleepTimeOut);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Mimics an {@code ElementExistsWaiter} to determine whether a list of elements exist on the page or not.
     *
     * @see org.openqa.selenium.support.ui.WebDriverWait
     */
    private class ElementCountFinderWaiter {
        
        private final Clock clock = new SystemClock();
        private final long testTimeOut = 1000;
        private final long sleepTimeOut = 500;
        
        /**
         * Mimics the {@code ElementExistsWaiter.until(..)} method and waits until the number of returned elements is greater than zero.  If the number of
         * returned elements becomes greater than zero within a certain time, then this method will return the element list, otherwise, it will return an empty 
         * list
         *
         * @param exists the function to evaluate
         * 
         * @see org.openqa.selenium.support.ui.Wait#until(com.google.common.base.Function)
         */
        public List<WebElement> until(Function<WebDriver, List<WebElement>> elements) {
            long end = clock.laterBy(testTimeOut);
            while (clock.isNowBefore(end)) {
                List<WebElement> values = elements.apply(driver);
                
                if (values != null && values.size() > 0) {
                    return values;
                }
                
                sleep();
            }
            
            return Collections.<WebElement>emptyList();
        }
        
        private void sleep() {
            try {
                Thread.sleep(sleepTimeOut);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public WebElement waitFor(final By theBy) {
        return wait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    final List<WebElement> elements = driver.findElements(theBy);
                    if (!elements.isEmpty()) {
                        return elements.get(0);
                    }
                    return null;
                }
            });
    }

    protected PortalPage loginAs(final String user) {
        clickMainMenu();

        driver.switchTo().defaultContent();

        final WebElement button = driver.findElement(By.xpath("//*[@value='Logout']"));
        button.click();
        return new PortalPage(driver);
    }

    protected PortalPage clickMainMenu() {
        driver.switchTo().defaultContent();

        final WebElement button = driver.findElement(By.linkText("Main Menu"));
        button.click();
        return new PortalPage(driver);
    }
}

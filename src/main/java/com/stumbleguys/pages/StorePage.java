package com.stumbleguys.pages;

import com.stumbleguys.elementActions.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class StorePage {

    private final Element element = new Element();

    private final By cards         = By.cssSelector(".Card_card__13mhK");
    private final By cardPriceBtn  = By.cssSelector(".Card_card__price_button__cZx7e");

    @Step("Verify shop page is loaded (cards visible)")
    public boolean isStoreLoaded() {
        element.isPageLoaded();
        return element.isVisible(cards, 15);
    }

    @Step("Get number of available shop cards")
    public int getBundleItemCount() {
        try {
            List<WebElement> items = element.findAll(cards);
            return items.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Step("Scroll to first shop card")
    public void selectFirstBundle() {
        element.scrollIntoView(cards);
    }

    @Step("Verify price/buy button is visible on first card")
    public boolean isBuyButtonVisible() {
        return element.isVisible(cardPriceBtn, 10);
    }

    @Step("Click buy button on first card")
    public void clickBuyButton() {
        element.scrollIntoView(cardPriceBtn);
        element.click(cardPriceBtn);
    }

}

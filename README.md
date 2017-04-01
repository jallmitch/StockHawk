# Stock Hawk

## Udacity Project # 3 - Stock Hawk
The objective of this project was to have students fix issues based on user feedback.

### Accessibility
#### Hellen
"Right now I can't use this app with my screen reader. My friends love it, so I would love to download it, but the buttons don't tell my screen reader what they do."
#### Solution
To allow the app to have the ability to be accessible to this user, all content needed to be able to be read by a screen reader. To accomplish this task, the following method was added to each `View`.
Example from the code:
```
StockDetailFragment.java
TextView bidPriceTV = (TextView) getActivity().findViewById(R.id.stock_bid);
....
 priceTV.setContentDescription(getContext().getString(R.string.stock_list_current_price) + " " + priceLabel);
```

```
activity_main.xml
    <android.support.design.widget.FloatingActionButton
        .....
        android:contentDescription="@string/activity_main_add_stock"
        .....
        />
```
### External Libraries
#### Adebowale
"Stock Hawk allows me to track the current price of stocks, but to track their prices over time, I need to use an external program. It would be wonderful if you could show more detail on a stock, including its price over time."
#### Solution
MPandroidChart was the selected library to be used to display a stocks values over a period of 2 years. Inside of this library was the CandleStickChart, which allows a person to see the four parts of the stock: Open, Close, High and Low for a particular day. 
### Localization
#### Your Boss
"We need to prepare Stock Hawk for the Egypt release. Make sure our translators know what to change and make sure the Arabic script will format nicely."
#### Solution
First place to make the app support localization is in the AndroidManifest.xml file, by adding the following line of code:
```
<application
.....
android:supportsRtl="true"
..... />
```
For  the UI a separate folder was created to make the layout work. This is found in the following directory
/app/src/main/res/layout-ldrtl

### Widget Support
#### Gundega
"I use a lot of widgets on my Android device, and I would love to have a widget that displays my stock quotes on my home screen."
#### Solution
Widget support was added. The widget showed each individual detailed report of the stock as a card, that could be rotated through.

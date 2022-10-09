package com.example.toursimapp.Data;

import com.example.toursimapp.Models.FaqQuestionsModel;
import com.example.toursimapp.R;

import java.util.ArrayList;

public class TripList {

    public ArrayList<String> guest_trip_array() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Friends");
        arrayList.add("Solo");
        return arrayList;
    }

    public ArrayList<String> all_trips_db() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("family_trip");
        arrayList.add("friends_trip");
        arrayList.add("honeymoon_trip");
        arrayList.add("religious_trip");
        arrayList.add("solo_trip");
        return arrayList;
    }

    public ArrayList<String> all_trips_names() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Family");
        arrayList.add("Friends");
        arrayList.add("Honeymoon");
        arrayList.add("Religious");
        arrayList.add("Solo");
        return arrayList;
    }

    public ArrayList<String> all_trips_home_titles() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Some Family trips available");
        arrayList.add("Some best spots to enjoy with your friends");
        arrayList.add("Some best honeymoon places to visit");
        arrayList.add("Some religious places where most visits");
        arrayList.add("Some pleasant places to visit for your solo trip");
        return arrayList;
    }

    public ArrayList<Integer> all_trip_images() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(R.drawable.family_img);
        arrayList.add(R.drawable.friends_img);
        arrayList.add(R.drawable.honeymoon_img);
        arrayList.add(R.drawable.reli_img);
        arrayList.add(R.drawable.solo_img);
        return arrayList;
    }

    public ArrayList<FaqQuestionsModel> faq_data_array() {
        ArrayList<FaqQuestionsModel> arrayList = new ArrayList<>();
        arrayList.add(new FaqQuestionsModel("Do I need to register to use this app?", "No. Registration is only required if you want to book nearby hotels in this app. \n\nIf you want to use the app only for finding city information and nearby hotel information you do not need to register, you can enter this app as a guest. \n\nIf you would like to register to book hotels and use more features of the app, go to profile page and register yourself by filling the registration form."));
        arrayList.add(new FaqQuestionsModel("How do I know that my booking is confirmed? ", "On booking you'll get a notification of booking in your phone and the message of confirmation would be shown in the inbox of the app and you'll be provided with an invoice in the booking details which would include your booking number and travel details."));
        arrayList.add(new FaqQuestionsModel("Is TourBuddy a safe application to make online bookings?", "Yes, we are committed to protecting our customer’s information. Even though the payment session is in the testing face, we ensure that the app will provide you will high security and you don't have to worry about any data leak or money hijacking. \n\nOur staff of security technology professionals uses a range of the best security technology available. Your information is secured from unauthorized access from the Internet. \nWe continually assess new technology for protecting your information to make sure that our information handling practices are in accordance with the highest industry standards and best practices on the Internet."));
        arrayList.add(new FaqQuestionsModel("What is our privacy policy?", "TourBuddy undertakes all possible measures to ensure that your personal details are not compromised. Please refer to our Privacy Policy outlines for measures undertaken by us to protect your personal details."));
        arrayList.add(new FaqQuestionsModel("What services do we offer?", "We provide service like providing information of various places you can visit in India and providing nearby hotels details and booking of the hotels."));
        arrayList.add(new FaqQuestionsModel("What star rating are your hotels?", "We provide minimum Standard category hotels which mean 3-star hotels, superior or First class of hotels are 4 star or equivalent and deluxe hotels which are 5 star or 5-star deluxe hotels. \n\nAt few places, deluxe or first-class hotels are not there and, in such case, we shall provide best available hotels which may not confirm to deluxe or first-class category. We also include heritage / palace and charming boutique hotels in our packages and can send you special cost for tours with hotels as per your choice."));
        arrayList.add(new FaqQuestionsModel("What is TourBuddy Credit Card Security Information?", "Your credit card security is our priority while you transact with us. We have put in place many checks that ensure complete security for your credit card. We are keen to share this with you to reassure you of our commitment to your credit card data security."));
        arrayList.add(new FaqQuestionsModel("Network Security", "At a network level, your card information is protected by Secured Socket Layer (SSL) technology, provide by Thawte, the encryption technology industry leader. \n\nAll IP (Internet Protocol) addresses are tracked and stored. They are required to locate people who may use a card for fraudulent activities."));
        arrayList.add(new FaqQuestionsModel("Physical Security", "Our Customer Service Executives are not allowed to carry their mobile phones or any other personal belonging on the work floor. \n\nNote pads given to Customer Service Executives is serial numbered and shredded in that order. \n\nAccess to shopping websites is barred on the work floor."));
        arrayList.add(new FaqQuestionsModel("Third party authorisation", "For all transactions where the name of the card holder and the passenger differ, we insist on a Third-Party Verification (TPA) process. The TPA is an internationally accepted methodology to check credit card fraud. Here we request the card holder to verify their card details and signatures in a faxed format, thus ensuring that the card holder is aware of the transaction. \n\nInsist on a telephone landline and an address to facilitate investigations in the future, if required. \n\nCross check the validity of the transaction with the respective banker / card issuer. The Banker / Card issuer may call the call holder to authenticate this transaction. \n\nWe realise that some of these security processes look cumbersome. However, our interest is not only in providing you the best-in-class travel product but also in ensuring that your data and security is truly protected. Hence, we urge you to bear with us and help us to help you."));
        return arrayList;
    }

}

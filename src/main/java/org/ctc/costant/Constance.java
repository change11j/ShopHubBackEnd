package org.ctc.costant;

public  class Constance {

    public final static String SHOP = "shop";

    public final static String USER_TYPE = "u";

    public final static String PRODUCT_TYPE = "p";
    public final static String CATEGORY_TYPE="c";
    public final static Integer IMAGE_UPLOAD_LIMIT=5;

    public final static Integer SHIP_METHOD_PLATFORM=0; //宅配
    public final static Integer SHIP_METHOD_SEVEN=1; // 7-Eleven
    public final static Integer SHIP_METHOD_FAMILY=2; // Family Mart

    public final static Integer PENDING_SHIPMENT = 0; // 待出货
    public final static Integer IN_TRANSIT = 1;       // 運送中
    public final static Integer COMPLETED = 2;        // 已完成
    public final static Integer CANCEL = 3;        // 取消
    public final static Integer RATING = 4;        // 待評價
    public final static Integer RATED = 5;        // 已評價

    public final static String[] RANDON_STRING= {"a","b","c","d","e","f","g","h","i","j","k"
            ,"l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
            ,"A","B","C","D","E","F","G","H","I","J","K","L","M","N"
            ,"O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2"
            ,"3","4","5","6","7","8","9"};

    public final static Integer VERIFY_EXPIRED_TIME = 10;

    public final static Integer ORDER_EXPIRED_TIME = 30;

    public final static Integer GOOGLE_OPEN = 1;

    public final static Integer GOOGLE_CLOSE = 0;
    public final static Integer SUCCESS = 200;

    public static final Integer EMAIL_ALREADY_EXIST = 2001;
    public static final Integer USER_NOT_EXIST = 2002;

    public static final Integer EMAIL_NOT_EXIST = 2003;

    public static final Integer VERIFY_CODE_WRONG = 2004;
    public static final Integer ADDRESS_NOT_EXIST = 2005;


    //Habit Constance

    public static final Integer HABIT_TYPE_NUMBER = 0;

    public static final Integer HABIT_TYPE_NOT_NUMBER = 1;
    public static final Integer HABIT_ALREADY_EXIST = 3001;
    public static final Integer HABIT_NOT_EXIST = 3002;

    public static final Integer HABIT_VALUE_INVALID = 3002;


    public static final String AUTHORIZATION_PROPERTY = "Authorization";

    public static final Integer JWT_EXPIRED = 4001;

    public static final Integer FILE_UPLOAD_ERROR = 10001;

    public static final Integer PARSE_DATE_ERROR =9001 ;
    public static final Integer UNKNOWN_ERROR = 9999;

}

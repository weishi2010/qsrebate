$(function () {

    navScroll();


    $('.item').on('click', function () {
        console.log(3);
    });

    //初始化
    var counter = 1;

    promotionTab = $("#promotionTab").val();
    if (1 == promotionTab) {
        //  Waterfall();
    }

    //首次加载
    loadProductData(counter, '', promotionTab);
    //监听加载更多
    $(window).scroll(function () {
        var totalheight = parseFloat($(window).height()) + parseFloat($(window).scrollTop());
        if ($(document).height() <= totalheight) {
            counter++;
            loadProductData(counter, '', promotionTab);
        }
    });

});

function navScroll() {
    //得到各种元素
    var nav = document.querySelector(".nav-nav");
    var navul = document.querySelector(".nav-nav ul");
    var navullis = document.querySelectorAll(".nav-nav ul li");

    var navW = parseInt(window.getComputedStyle(nav, null)['width']);

    var navulW = 0;
    for (var i = 0; i < navullis.length; i++) {
        //点击加活动类
        navullis[i].onclick = function () {
            $('.nav-nav ul li').removeClass('active');
            this.classList.add('active');
        }

        navulW += parseInt(window.getComputedStyle(navullis[i], null)['width']);
    }

    //宽度
    navul.style.width = navulW + "px";

    nav.addEventListener("touchstart", touchstartHandler);
    nav.addEventListener("touchmove", touchmoveHandler);
    nav.addEventListener("touchend", touchendHandler);


    var startX;
    var nowX = 0;
    var dX;

    var lastTwoPoint = [0, 0];

    //开始滑动
    function touchstartHandler(event) {
        navul.style.webkitTransition = "none";	//去掉过渡
        navul.style.transition = "none";	//去掉过渡
        startX = event.touches[0].pageX;	//记录起点
    }

    //滑动过程
    function touchmoveHandler(event) {
        event.preventDefault();
        dX = event.touches[0].pageX - startX;	//差值

        //反映差值
        navul.style.webkitTransform = "translateX(" + (nowX + dX) + "px)";
        navul.style.transform = "translateX(" + (nowX + dX) + "px)";

        //记录最后两点的x值
        lastTwoPoint.shift();
        lastTwoPoint.push(event.touches[0].pageX);
    }

    //结束滑动
    function touchendHandler(event) {
        nowX += dX;

        //多走最后两点路程的5倍路程
        nowX += (lastTwoPoint[1] - lastTwoPoint[0]) * 1;
        if (nowX > 0) {
            nowX = 0;
        }

        if (nowX < -navulW + navW) {
            nowX = -navulW + navW;
        }

        //过渡时间
        //非线性衰减
        var t = Math.sqrt(Math.abs(lastTwoPoint[1] - lastTwoPoint[0])) / 10;

        navul.style.webkitTransition = "all " + t + "s cubic-bezier(0.1, 0.85, 0.25, 1) 0s";
        navul.style.transition = "all " + t + "s cubic-bezier(0.1, 0.85, 0.25, 1) 0s";

        //反映多走的5倍路程：
        navul.style.webkitTransform = "translateX(" + nowX + "px)";
        navul.style.transform = "translateX(" + nowX + "px)";
    }
}

function Waterfall() {

    var html_fz = parseInt($('html').eq(0).css('font-size'));
    var win_w = $(window).width() - .4 * html_fz;
    var obj = $('.must-buy-list .item');
    var item_w = obj.width();

    var Column = Math.ceil(win_w / item_w);

    for (var i = 0; i <= obj.length - 1; i++) {

        var num = 0;

        for (j = i - Column; j >= 0; j = j - Column) {
            num = num + obj.eq(j).height() + .2 * html_fz;
        }

        obj.eq(i).css({
            "top": num + 'px',
            "left": i % Column * item_w + 'px',
        });

    }

    //样式
    var h = parseInt($('.must-buy-list .item').last().css('top')) + $('.must-buy-list .item').last().height();
    $('.must-buy-list').css('height', h + 'px');

}

//1为每日购买 2为9.9秒杀
function reLoadData(page, secondCategoryList, tab) {
    $('.g-list').html("");

    loadProductData(page, secondCategoryList, tab);
}

function loadProductData(page, secondCategoryList, tab) {
    $.ajax({
        type: 'GET',
        url: '/promotion/products.json?page=' + page + "&secondCategoryList=" + secondCategoryList + "&tab=" + tab + "&r=" + Math.random(),
        dataType: 'json',
        success: function (reponse) {
            var list = reponse.products;
            var sum = list.length;
            var result = '';
            for (var i = 0; i < sum; i++) {
                result += getTemplate(list[i], tab);
            }
            $('.g-list').append(result);
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });

}


function searchProducts() {
    page = 1
    searchParam = $("#searchParam").val();
    searchParam = encodeURIComponent(encodeURIComponent(searchParam))
    $.ajax({
        type: 'GET',
        url: '/promotion/searchProducts.json?page=1&params=' + searchParam + '&r=' + Math.random(),
        dataType: 'json',
        success: function (reponse) {
            var list = reponse.products;
            var sum = list.length;
            var result = '';
            for (var i = 0; i < sum; i++) {
                result += getTemplate(list[i]);
            }
            $('.g-list').html('');

            $('.g-list').append(result);
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });

}

function getTemplate(product, tab) {
    var htmlTemp = "";
    if (product.couponType == 2 && product.productCoupon) {
        htmlTemp += "<div class=\"item mui-flex\" productId='" + product.productId + "'>" +
            "        <div class=\"g-img cell fixed\">";
        htmlTemp += "  <a href=\"javascript:void(0);\" onclick=\"redirectJdPromotionCouponUrlBySui(" + product.productId + ")\" ><img class=\"g-img\" src=\"" + product.imgUrl + "\" alt=\"" + product.name + "\" width='90' height='90'></a>";
    } else {
        htmlTemp += "<div class=\"item mui-flex\" productId='" + product.productId + "'>" +
            "        <div class=\"g-img cell fixed\">";
        htmlTemp += "  <a href=\"javascript:void(0);\" onclick=\"redirectJdPromotionUrlBySui(" + product.productId + ")\" ><img class=\"g-img\" src=\"" + product.imgUrl + "\" alt=\"" + product.name + "\" width='90' height='90'></a>";
    }
    htmlTemp += " </div> " +
        "<div class=\"cnt cell align-center\">";
    if (product.couponType == 2 && product.productCoupon) {
        htmlTemp += "<a href=\"javascript:void(0);\" onclick=\"redirectJdPromotionCouponUrlBySui(" + product.productId + ")\" class=\"tl\">" + product.name + "</a>";
    } else {
        htmlTemp += "<a href=\"javascript:void(0);\" onclick=\"redirectJdPromotionUrlBySui(" + product.productId + ")\" class=\"tl\">" + product.name + "</a>";
    }

    htmlTemp += " <div class=\"meta\">";
    if(product.freePost==1){
        htmlTemp +=  "                <span class=\"baoyou\">包邮</span>";
    }
    if (product.productCoupon) {
        htmlTemp += " <span class=\"quan\">券:" + product.productCoupon.discount + "</span>";
    }
    htmlTemp += "</div>";

    htmlTemp += "            <div class=\"easy\">";

    if (product.productCoupon) {
        htmlTemp += "                    <span class='coupon_price'>￥" + product.productCoupon.couponPrice + "</span><del class=\"old_price\">￥" + product.originalPrice + "</del>";
    }
    htmlTemp +=
        // "                </a>" +
        "                <span class=\"share\"><a href =\"javascript:void(0);\" onclick=\"redirectJdPromotionCouponUrlBySui(" + product.productId + ")\" >" +
        "                    <img class=\"zhuanfa\" src=\"/static/img/ico-cart-01.png\" alt=\"\">" +
        "                    去购买"+
        "                </a></span>" +
        "            </div>";

    htmlTemp += "        </div>" +
        "    </div>";
    return htmlTemp;
}

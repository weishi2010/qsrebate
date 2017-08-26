$(function () {

    navScroll();


    //初始化
    var counter = 1;

    promotionTab = $("#promotionTab").val();
    if(1==promotionTab){
        Waterfall();
    }

    //首次加载
    loadProductData(counter,-1,promotionTab);
    //监听加载更多
    $(window).scroll(function () {
        var totalheight = parseFloat($(window).height()) + parseFloat($(window).scrollTop());
        if ($(document).height() <= totalheight) {
            counter++;
            loadProductData(counter,-1,promotionTab);
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
    $('.must-buy-list').css('height', h+'px');

}

//1为每日购买 2为9.9秒杀
function reLoadData(page,category,tab){
    loadProductData(page,category,tab);
}

function reLoadData(page,category,tab){
    if (1 == tab) {
        $('.must-buy-list').html("");
    } else if (2 == tab) {
        $('.g-list').html("");
    }

    loadProductData(page,category,tab);
}

function loadProductData(page,category,tab) {

    $.ajax({
        type: 'GET',
        url: '/rebate/products.json?page='+page+"&thirdCategory="+category+"&tab="+tab,
        dataType: 'json',
        success: function (reponse) {
            var list = reponse.products;
            var sum = list.length;
            var result = '';
            for (var i = 0; i < sum; i++) {
                result += getTemplate(list[i],tab);
            }
            if (1 == tab) {
                $('.must-buy-list').append(result);
            } else if (2 == tab) {
                $('.g-list').append(result);
            }
            //刷新样式
            Waterfall();
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });

    function getTemplate(product,tab){
        var htmlTemp = "";
        if(1==tab) {

            htmlTemp = "<div class=\"item\" >" +
                "                <div class=\"inner\">" +
                "                    <a href=\"https://item.m.jd.com/product/" + product.productId + ".html\" title=\"+list[i].name+\"><img class=\"g-img\" src=\"" + product.imgUrl + "\" alt=\"" + product.name + "\" width='150' height='150'></a>" +
                "                    <div class=\"title\">" +
                "                        <a href=\"https://item.m.jd.com/product/" + product.productId + ".html\" title=''" + product.name + "><img src=\"/static/img/ico-jd-01.png\">" + product.name + "</a>" +
                "                        <!--<div class=\"have-buy\">(已售33件)</div>-->" +
                "                    </div>" +
                "                    <div class=\"meta\">" +
                "                        <div class=\"old-price\">原价¥<del>" + product.originalPrice + "</del></div>" +
                "                        <!--<div class=\"now-price\">限时抢:<span>15.9</span></div>-->" +
                "                    </div>" +
                "                    <div class=\"easy\">" +
                "                        <div class=\"item1\"><img src=\"/static/img/ico-cart-01.png\" alt=\"\"></div>" +
                "                        <div class=\"item1\"><img src=\"/static/img/ico-zhuanfa-01.png\" alt=\"\"></div>" +
                "                    </div>" +
                "                    <div class=\"total-money\">" +
                "                        <p class=\"fan\">买就返¥" + product.commission + "</p>" +
                "                        <p class=\"share\">分享再赚¥1</p>" +
                "                    </div>" +
                "                </div>" +
                "            </div>";
        }else if(2==tab){
            htmlTemp = "<div class=\"item mui-flex\">" +
                "        <div class=\"g-img cell fixed\">" +
                "             <a href=\"https://item.m.jd.com/product/" + product.productId + ".html\" ><img class=\"g-img\" src=\"" + product.imgUrl + "\" alt=\"" + product.name + "\" width='64' height='87'></a>" +
                "        </div>" +
                "        <div class=\"cnt cell align-center\">" +
                "            <a href=\"https://item.m.jd.com/product/" + product.productId + ".html\" class=\"tl\">"+product.name+"</a>" +
                "            <div class=\"meta\">" +
                "                <span class=\"old-price\">原价：<del>"+product.originalPrice+"</del></span>" +
                "                <!--<span class=\"new-price\">劵后价：3050.00元</span>-->" +
                "            </div>" +
                "            <div class=\"easy\">" +
                "                <a href=\"javascript:;\" class=\"buy\">" +
                "                    <img class=\"cart\" src=\"/static/img/ico-cart-01.png\" alt=\"\">" +
                "                    购买返：¥5.00" +
                "                </a>" +
                "                <a href=\"javascript:;\" class=\"share\">" +
                "                    <img class=\"zhuanfa\" src=\"/static/img/ico-zhuanfa-01.png\" alt=\"\">" +
                "                    分享赚：¥1.00" +
                "                </a>" +
                "            </div>" +
                "        </div>" +
                "    </div>";
        }
        return htmlTemp;
    }
}

(function () {
    document.addEventListener('DOMContentLoaded', function () {
        var html = document.documentElement;
        var windowWidth = html.clientWidth;
        html.style.fontSize = windowWidth / 7.5 + 'px';  //设计稿横向宽度是750px
    }, false);
})();
window.onresize = function () {
    reset_font();
    navScroll();
    Waterfall();
};
function reset_font() {
    var html = document.getElementsByTagName('html')[0];
    var win_w = document.documentElement.clientWidth;
    var scale = win_w / 750;
    html.style.fontSize = 100 * scale + 'px';
}

$(function () {


    $('.radio').on('click', function () {
        if ($(this).hasClass('radio-single')) {
            $('.radio-single').removeClass('chk');
            $(this).addClass('chk');
        } else {
            $(this).toggleClass('chk');
        }
    });

    $('.header .title').on('click', function () {
        var $this = $(this);
        if ($(this).parents('.header').find('.droplist').hasClass('droplist-expand')) {
            $(this).parents('.header').find('.droplist').removeClass('droplist-expand');
            $('body').css({'overflow': 'auto'});
        } else {
            $(this).parents('.header').find('.droplist').addClass('droplist-expand');
            $('body').css({'overflow': 'hidden'});
        }
        //单选
        $('.header .droplist .sort').on('click', function () {
            var val = $(this).html();
            $(this).addClass('selected').siblings().removeClass('selected');
            $(this).parents('.header').find('.droplist').removeClass('droplist-expand');
            $('body').css({'overflow': 'auto'});
            $this.find('.text').html(val);
        });
    });

    $('.tabs-tabs .arrow').on('click', function () {
        if ($(this).parents('.tabs-tabs-wrap').find('.tabs-droplist').hasClass('tabs-droplist-expand')) {
            $(this).parents('.tabs-tabs-wrap').find('.tabs-droplist').removeClass('tabs-droplist-expand');
            $(this).find('img').attr('src', '/static/img/ico-arrow-down-02.png');
        } else {
            $(this).parents('.tabs-tabs-wrap').find('.tabs-droplist').addClass('tabs-droplist-expand');
            $(this).find('img').attr('src', '/static/img/ico-arrow-up-02.png');
        }
    });
    $('.tabs-tabs-wrap .list li').on('click',function(){
       $(this).parents('.tabs-droplist').removeClass('tabs-droplist-expand');
        $(this).parents('.tabs-tabs-wrap').find('img').attr('src', '/static/img/ico-arrow-down-02.png');
    });

    $('.index-alert .alert-face').on('touchend', function () {
        if ($(this).parents('.index-alert').find('.alert-cnt').hasClass('alert-expand')) {
            $(this).parents('.index-alert').find('.alert-cnt').removeClass('alert-expand');
            $(this).parents('.index-alert').find('.alert-shade').hide();
            $(this).find('span').html('打开');
            $('body').css({'overflow': 'auto'});
            $(this).find('img').attr('src', '/static/img/ico-arrow-down-03.png');
        } else {
            $(this).parents('.index-alert').find('.alert-cnt').addClass('alert-expand');
            $(this).parents('.index-alert').find('.alert-shade').show();
            $(this).find('span').html('关闭');
            $('body').css({'overflow': 'hidden'});
            $(this).find('img').attr('src', '/static/img/ico-arrow-up-03.png');
        }
    });

    $('.footer a').on('click', function () {
        $(this).addClass('active').siblings().removeClass('active');
    });

});


function redirectJdPromotionUrl(skuId) {
    $.ajax({
        type: 'GET',
        url: '/product/jdShortUrl.json?skuId=' + skuId + "&r=" + Math.random(),
        dataType: 'json',
        success: function (reponse) {
            var url = reponse.url;
            location.href = url;
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });
}

function redirectJdPromotionCouponUrl(skuId,couponLink) {
    $.ajax({
        type: 'GET',
        url: '/product/getPromotionCouponCode.json?skuId=' + skuId + "&couponLink="+couponLink+"&r=" + Math.random(),
        dataType: 'json',
        success: function (reponse) {
            if(reponse.success){
                var url = reponse.url;
                location.href = url;
            }else{
                alert("很抱歉，优惠券活动已经结束！")
            }
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });
}
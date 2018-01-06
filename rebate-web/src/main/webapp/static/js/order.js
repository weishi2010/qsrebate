$(function () {

    var Uarry=$("#sorts li");//获取所有的li元素
    $("#sorts li").click(function(){//点击事件

        var count=$(this).index();//获取li的下标
        var tab = Uarry.eq(count).attr("data");
        $("#tab").val(tab);
        loadOrderData(1);
    })

    //初始化
    var counter = 1;


    //首次加载
    loadOrderData(counter);
    //监听加载更多
    $(window).scroll(function () {
        var totalheight = parseFloat($(window).height()) + parseFloat($(window).scrollTop());
        if ($(document).height() <= totalheight) {
            counter++;
            loadOrderData(counter);
        }
    });

});


function loadOrderData(page) {
    var tab = $("#tab").val();
    if(1==page){
        $('.o-list').html("");
    }
    $.ajax({
        type: 'GET',
        url: '/personal/orders.json?tab='+tab+'&page='+page+"&r="+Math.random(),
        dataType: 'json',
        success: function (reponse) {
            var list = reponse.detailList;
            var tab = reponse.tab;
            var sum = list.length;
            var result = '';
            for (var i = 0; i < sum; i++) {
                result += getTemplate(list[i],tab);
            }
            $('.o-list').append(result);
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });

    function getTemplate(detail,tab){
        var statusTemp = "";
        if(1==detail.orderStatus){
            if(1==detail.status){
                statusTemp="<div class=\"ft\" style='float: right'><div class=\"state-ok\">已经结算</div></div>";
            }else{
                statusTemp="<div class=\"ft\" style='float: right'><div class=\"state-wait\">未结算</div></div>";
            }
        }else{
            statusTemp="<div class=\"ft\" style='float: right'><div class=\"state-wait\">已取消</div></div>";
        }

        var htmlTemp =  "<div class=\"item\">" +
            "        <div class=\"hd\">" +
            "            <div class=\"o-number\">订单号："+detail.orderId+" </div>" +
             statusTemp +
            "        </div>" +
            "        <div class=\"o-item mui-flex\">" +
            "            <div class=\"g-img cell fixed\">" +
            "               <a href='javascript:void(0);' onclick='redirectJdPromotionUrl(" + detail.productId + ")' ><img src='" + detail.imgUrl + "' alt='" + detail.productName + "' /></a>" +
            "            </div>" +
            "            <div class=\"cnt cell\">" +
            "                <div class=\"tl\">" +
            "                   <a href=\"javascript:void(0);\" onclick=\"redirectJdPromotionUrl(" + detail.productId + ")\" class=\"tl\">" + detail.productName + "</a>" +
            "                </div>" +
            "                <!--<div class=\"money-scale\">返钱比例："+detail.platformRatio+"%</div>-->" +
            "            </div>" +
            "        </div>" +
            "        <div class=\"info mui-flex \">" +
            "            <div class=\"item cell\">" +
            "                <p>购买数量</p>" +
            "                <span>"+detail.productCount+"</span>" +
            "            </div>" +
            "            <div class=\"item cell\">" +
            "                <p>购买金额</p>" +
            "                <span>¥"+detail.price+"</span>" +
            "            </div>";
                if(0==tab) {
                    htmlTemp+="            <div class=\"item cell\">" +
                    "                <p>可返钱</p>" +
                    "                <span>¥" + detail.userCommission + "</span>" +
                    "            </div>";
                }
        htmlTemp+="        </div>" +
            "        <div class=\"ft\">"+
        "            <div class=\"return-m\" style='float: left'>下单时间："+detail.submitDateShow+"</div>" +
        "            <div class=\"return-m\">";
            if(0!=tab){
                htmlTemp+="预估返钱：¥<b>"+detail.agentCommission+"</b>";
            }else{
                htmlTemp+="预估返钱：¥<b>"+detail.userCommission+"</b>";
            }

        htmlTemp+="            </div>" +
            "        </div>" +
            "    </div>";
        return htmlTemp;
    }
}

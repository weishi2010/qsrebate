$(function () {


    $("#sorts li").click(function(){//点击事件

        var count=$(this).index();//获取li的下标
        var days = Uarry.eq(count).data;
        $("#days").val(days);
        loadOrderData(days);
    })

    //初始化
    var counter = 1;


    //首次加载
    loadOrderData(30);
    //监听加载更多
    $(window).scroll(function () {
        var totalheight = parseFloat($(window).height()) + parseFloat($(window).scrollTop());
        if ($(document).height() <= totalheight) {
            counter++;
            var days = $("#days").val();
            loadOrderData(days);
        }
    });

});


function loadOrderData(days) {

    $.ajax({
        type: 'GET',
        url: '/rebate/personal/orders.json?days='+days,
        dataType: 'json',
        success: function (reponse) {
            var list = reponse.detailList;
            var sum = list.length;
            var result = '';
            for (var i = 0; i < sum; i++) {
                result += getTemplate(list[i]);
            }
            $('.o-list').append(result);
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });

    function getTemplate(detail){
        var htmlTemp =  "<div class=\"item\">" +
            "        <div class=\"hd\">" +
            "            <div class=\"o-number\">订单号："+detail.orderId+"</div>" +
            "        </div>" +
            "        <div class=\"o-item mui-flex\">" +
            "            <div class=\"g-img cell fixed\">" +
            "                <img src=\""+detail.productImg+"\" alt=\"\">" +
            "            </div>" +
            "            <div class=\"cnt cell\">" +
            "                <div class=\"tl\">" +
            "                    <span class=\"tag\">同店</span>" +detail.productName+
            "                </div>" +
            "                <div class=\"money-scale\">返钱比例："+detail.commissionRatio+"</div>" +
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
            "            </div>" +
            "            <div class=\"item cell\">" +
            "                <p>可返钱</p>" +
            "                <span>¥"+detail.commission+"</span>" +
            "            </div>" +
            "        </div>" +
            "        <div class=\"ft\">" +
            "            <div class=\"state-ok\">已确认</div>" +
            "            <div class=\"return-m\">" +
            "                预估返钱：¥<b>"+detail.commission+"</b>" +
            "            </div>" +
            "        </div>" +
            "    </div>";
        return htmlTemp;
    }
}

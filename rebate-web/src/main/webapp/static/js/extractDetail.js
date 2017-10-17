$(function () {


    $("#sorts li").click(function(){//点击事件

        var count=$(this).index();//获取li的下标
        var year = Uarry.eq(count).data;
        $("#year").val(year);
        loadExtractData(year);
    })

    //初始化
    var counter = 1;
    var myDate = new Date();
    var year = myDate.getFullYear(); //获取当前年份(2位)
    //首次加载
    loadExtractData(year);
    //监听加载更多
    $(window).scroll(function () {
        var totalheight = parseFloat($(window).height()) + parseFloat($(window).scrollTop());
        if ($(document).height() <= totalheight) {
            counter++;
            var year = $("#year").val();
            loadExtractData(days);
        }
    });

});


function loadExtractData(year) {

    $.ajax({
        type: 'GET',
        url: '/personal/getExtractDetails.json?year='+year+"&r="+Math.random(),
        dataType: 'json',
        success: function (reponse) {
            var list = reponse.detailList;
            var sum = list.length;
            var result = '';
            for (var i = 0; i < sum; i++) {
                result += getTemplate(list[i]);
            }
            $('#extractDetails').append(result);
        },
        error: function (xhr, type) {
            console.log('加载更多数据错误！');
        }
    });

    function getTemplate(detail){
        var htmlTemp =  "        <tr>" +
            "            <td class=\"time\">"+detail.extractDate+"</td>" +
            "            <td>提现金额</td>" +
            "            <td><span class=\"orange\">"+detail.extractPrice+"元</span></td>" +
            "            <td class=\"state\"><span class=\"wait\">"+detail.extractStatusShow+"</span></td>" +
            "        </tr>";
        return htmlTemp;
    }
}

function showAddSecondAgent(){
    layer.open({
        title: [
            '下级代理推广'
        ]
        ,offset: ['100px']
        ,anim: 'up'
        ,content: '<div>佣金比例(%):<input type="text" id="commissionRatio" class="layui-layer-input" /></div>'
        ,btn: [ '生成推广二维码','取消']
        ,yes: function(index){
            var commissionRatio = $("#commissionRatio").val();
            if(isNaN(commissionRatio) || commissionRatio>100 ||commissionRatio<0){
                alert("填写的佣金比例应该在1-100%范围内!");
                return;
            }
            $.ajax({
                type: 'GET',
                url: '/personal/registNextAgentQrCode.json?commissionRatio='+commissionRatio+ '&r=' + Math.random(),
                dataType: 'json',
                success: function (reponse) {
                    if(reponse.success){
                        layer.close(index)
                        layer.alert("下级代理推广二维码已生成，请返回对话框查看!");
                    }else{
                        layer.alert("二维码生成失败，请稍后重试!");
                    }
                },
                error: function (xhr, type) {
                    console.log('加载更多数据错误！');
                }
            });

        }
        ,btn2: function(index){
            layer.close(index)
        }
    });
}

function showEdityLayer(id,commissionRatio){
    layer.open({
        title: [
            '代理比例设置'
        ]
        ,offset: ['100px']
        ,anim: 'up'
        ,content: '<div>佣金比例:<input type="text" id="agentCommissionRatio" class="layui-layer-input" value=" '+commissionRatio+'" /></div>'
        ,btn: [ '确认','取消']
        ,yes: function(index){
            var agentCommissionRatio = $("#agentCommissionRatio").val();
            $.ajax({
                type: 'GET',
                url: '/personal/updateSecondAgentCommissionRate.json?id='+id+'&commissionRatio='+agentCommissionRatio+ '&r=' + Math.random(),
                dataType: 'json',
                success: function (reponse) {
                    layer.close(index)
                    window.location.href=window.location.href;
                },
                error: function (xhr, type) {
                    console.log('加载更多数据错误！');
                }
            });

        }
        ,btn2: function(index){
            layer.close(index)
        }
    });
}


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


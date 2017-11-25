function showAddSecondAgent(){
    layer.open({
        title: [
            '添加下级代理'
        ]
        ,offset: ['100px']
        ,anim: 'up'
        ,content: '<div>子联盟 id:<input type="text" id="agentSubUnionId" class="layui-layer-input" /></div>' +
        '<br><div>佣金比例:<input type="text" id="commissionRatio" class="layui-layer-input" /></div>'
        ,btn: [ '确认','取消']
        ,yes: function(index){
            var agentSubUnionId = $("#agentSubUnionId").val();
            var commissionRatio = $("#commissionRatio").val();
            if(isNaN(commissionRatio) || commissionRatio>1 ||commissionRatio<0){
                alert("填写的佣金比例应该在0-1范围内!");
                return;
            }
            $.ajax({
                type: 'GET',
                url: '/personal/addSecondAgent.json?agentSubUnionId='+agentSubUnionId+'&commissionRatio='+commissionRatio+ '&r=' + Math.random(),
                dataType: 'json',
                success: function (reponse) {
                    if(reponse.success){
                        layer.close(index)
                        window.location.href=window.location.href;
                    }else if(reponse.code==2){
                        alert("此代理已存在!");
                    }else if(reponse.code==-2){
                        alert("不存在子联盟ID用户!");
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


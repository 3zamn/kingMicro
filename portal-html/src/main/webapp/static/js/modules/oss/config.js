
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
        config: {}
	},
    created: function(){
        this.getConfig();
    },
	methods: {
		getConfig: function () {
            $.getJSON(baseURL + "oss/file/config", function(r){
				vm.config = r.data;
            });
        },
		addConfig: function(){
			vm.showList = false;
			vm.title = "云存储配置";
		},
		saveOrUpdate: function () {
			var url = baseURL + "oss/file/saveConfig";
			$.ajax({
				type: "POST",
			    url: url,
                contentType: "application/json",
			    data: JSON.stringify(vm.config),
			    success: function(r){
			    	if(r.code === 200){
						alert('操作成功', function(){
						});
					}else{
						alert(r.msg);
					}
				}
			});
		}
	}
});
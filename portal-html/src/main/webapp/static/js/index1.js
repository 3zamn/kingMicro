layui.config({
  base:'static/js/'
}).use(['navtab'],function(){
	window.jQuery = window.$ = layui.jquery;
	window.layer = layui.layer;
    var element = layui.element,
	navtab = layui.navtab({
		elem: '.larry-tab-box'
	});

    //iframe自适应
	$(window).on('resize', function() {
		var $content = $('#larry-tab .layui-tab-content');
		$content.height($(this).height() - 140);
	    $content.find('iframe').each(function() {
	    	$(this).height($content.height());
	    });
	}).resize();
  
	$(function(){
	    $('#larry-nav-side').click(function(){
	        if($(this).attr('lay-filter')!== undefined){
	            $(this).children('ul').find('li').each(function(){
	                var $this = $(this);
	                if($this.find('dl').length > 0){
	                   var $dd = $this.find('dd').each(function(){
	                       $(this).on('click', function() {
	                           var $a = $(this).children('a');
	                           var href = $a.data('url');
	                           var icon = $a.children('i:first').data('icon');
	                           var title = $a.children('span').text();
	                           var data = {
	                                 href: href,
	                                 icon: icon,
	                                 title: title
	                           }
	                           navtab.tabAdd(data);
	                       });
	                   });
	                }else{
	                	$this.on('click', function() {
                           var $a = $(this).children('a');
                           var href = $a.data('url');
                           var icon = $a.children('i:first').data('icon');
                           var title = $a.children('span').text();
                           var data = {
                                 href: href,
                                 icon: icon,
                                 title: title
                           }
                           navtab.tabAdd(data);
	                    });
	                }
	            });
	        }
	    }).trigger("click");
	});
});


layui.use(['jquery','layer','element'],function(){
	window.jQuery = window.$ = layui.jquery;
	window.layer = layui.layer;
	var element = layui.element;

	// larry-side-menu向左折叠
	$('.larry-side-menu').click(function() {
	  var sideWidth = $('#larry-side').width();
	  if(sideWidth === 200) {
	      $('#larry-body').animate({
	        left: '0'
	      }); 
	      $('#larry-footer').animate({
	        left: '0'
	      });
	      $('#larry-side').animate({
	        width: '0'
	      });
	  } else {
	      $('#larry-body').animate({
	        left: '200px'
	      });
	      $('#larry-footer').animate({
	        left: '200px'
	      });
	      $('#larry-side').animate({
	        width: '200px'
	      });
	  }
	});
});



//生成菜单
var menuItem = Vue.extend({
	name: 'menu-item',
	props:{item:{}},
	template:[
			 /* '<li class="layui-nav-item">',*/    //收缩状态
	          '<li class="layui-nav-item layui-nav-itemed">',   //展开状态
	          '<a v-if="item.type === 0" href="javascript:;">',
	          '<i v-if="item.icon != null" :class="item.icon"></i>',
	          '<span>{{item.name}}</span>',
	          '<em class="layui-nav-more"></em>',
	          '</a>',
	          '<dl v-if="item.type === 0" class="layui-nav-child">',
	          '<dd v-for="item in item.list">',
	          '<a v-if="item.type === 1" href="javascript:;" :data-url="item.url"><i v-if="item.icon != null" :class="item.icon" :data-icon="item.icon"></i> <span>{{item.name}}</span></a>',
	          '</dd>',
	          '</dl>',
	          '<a v-if="item.type === 1" href="javascript:;" :data-url="item.url"><i v-if="item.icon != null" :class="item.icon" :data-icon="item.icon"></i> <span>{{item.name}}</span></a>',
	          '</li>'
	].join('')
});

//注册菜单组件
Vue.component('menuItem',menuItem);

var vm = new Vue({
	el:'#layui_layout',
	data:{
		user:{},
		menuList:{},
		password:'',
		newPassword:'',
        navTitle:"控制台"
	},
	methods: {
		getMenuList: function () {
			$.getJSON(baseURL + "sys/menu/nav", function(r){
				vm.menuList = r.menuList;
			//	debugger
                window.permissions = r.permissions;
			});
		},
		getUser: function(){
			$.getJSON(baseURL + "sys/user/info", function(r){
				vm.user = r.user;
			});
		},
		updatePassword: function(){
			layer.open({
				type: 1,
				skin: 'layui-layer-molv',
				title: "修改密码",
				area: ['550px', '270px'],
				shadeClose: false,
				content: jQuery("#passwordLayer"),
				btn: ['修改','取消'],
				btn1: function (index) {
					var data = "password="+vm.password+"&newPassword="+vm.newPassword;
					$.ajax({
						type: "POST",
					    url: baseURL + "sys/user/password",
					    data: data,
					    dataType: "json",
					    success: function(r){
							if(r.code == 200){
								layer.close(index);
								layer.alert('修改成功', function(){
									location.reload();
								});
							}else{
								layer.alert(r.msg);
							}
						}
					});
	            }
			});
		},
        logout: function () {
            $.ajax({
                type: "POST",
                url: baseURL + "sys/logout",
                dataType: "json",
                success: function(r){
                    //删除本地token
                    localStorage.removeItem("token");
                    //跳转到登录页面
                    location.href = clientURL + 'login.html';
                }
            });
        },
        personInfo: function(){
			layer.open({
				type: 1,
				skin: 'layui-layer-molv',
				title: "基本资料",
				area: ['550px', '310px'],
				shadeClose: false,
				content: jQuery("#personInfo"),
				btn: ['关闭']
			});
		},
	},
	created: function(){
		this.getMenuList();
		this.getUser();
	}
});

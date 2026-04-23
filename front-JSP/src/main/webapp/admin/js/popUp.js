(function($) {
/*---------------------------
 Defaults for briefPopUp
 至简弹窗
----------------------------*/
/*---------------------------
 Listener for BriefPopUp class
 监听BriefPopUp类的单击事件
----------------------------*/
    $(document).on("click", '.briefPopUp', function (event) {
        event.preventDefault();
        var modalLocation = $(this).attr('data-briefPopUp-id');
        $('#' + modalLocation).reveal($(this).data());
    });
/*---------------------------
 Extend and Execute
 扩展和执行
----------------------------*/
    $.fn.reveal = function(options) {
        var defaults = {
	    	animation: 'fadeAndPop', //fade, fadeAndPop, none
		    animationspeed: 300, //how fast animtions are
		    closeonbackgroundclick: true, //if you click background will modal close
		    dismissmodalclass: 'close-briefPopUp-modal' //the class of a button or element that will close an open modal
    	};

        //Extend dem' options 获取设置选项
        var options = $.extend({}, defaults, options);

        return this.each(function() {
/*---------------------------
 Global Variables
 全局变量
----------------------------*/
        	var modal = $(this),
        		topMeasure  = parseInt(modal.css('top')),
				topOffset = modal.height() + topMeasure,
          		locked = false,
				modalBG = $('.briefPopUp-background');

/*---------------------------
 Create Modal background
 加入透明黑色的背景
----------------------------*/
			if(modalBG.length == 0) {
			    modalBG = $('<div class="briefPopUp-background" />').insertAfter(modal);
			}

/*---------------------------
 Open & Close Animations
 打开和关闭事件
----------------------------*/
			//Entrance Animations  进场动画
			modal.bind('briefPopUp:open', function () {
				modalBG.unbind('click.modalEvent');
				$('.' + options.dismissmodalclass).unbind('click.modalEvent');
				if(!locked) {
					lockModal();
					if(options.animation == "fadeAndPop") {
						modal.css({'top': $(document).scrollTop()-topOffset, 'opacity' : 0, 'visibility' : 'visible'});
						modalBG.fadeIn(options.animationspeed/2);
						modal.delay(options.animationspeed/2).animate({
							"top": $(document).scrollTop()+topMeasure + 'px',
							"opacity" : 1
						}, options.animationspeed,unlockModal());
					}
					if(options.animation == "fade") {
						modal.css({'opacity' : 0, 'visibility' : 'visible', 'top': $(document).scrollTop()+topMeasure});
						modalBG.fadeIn(options.animationspeed/2);
						modal.delay(options.animationspeed/2).animate({
							"opacity" : 1
						}, options.animationspeed,unlockModal());
					}
					if(options.animation == "none") {
						modal.css({'visibility' : 'visible', 'top':$(document).scrollTop()+topMeasure});
						modalBG.css({"display":"block"});
						unlockModal()
					}
				}
				modal.unbind('briefPopUp:open');
			});
			//Closing Animation  关闭动画
			modal.bind('briefPopUp:close', function () {
			  if(!locked) {
					lockModal();
					if(options.animation == "fadeAndPop") {
						modalBG.delay(options.animationspeed).fadeOut(options.animationspeed);
						modal.animate({
							"top":  $(document).scrollTop()-topOffset + 'px',
							"opacity" : 0
						}, options.animationspeed/2, function() {
							modal.css({'top':topMeasure, 'opacity' : 1, 'visibility' : 'hidden'});
							unlockModal();
						});
					}
					if(options.animation == "fade") {
						modalBG.delay(options.animationspeed).fadeOut(options.animationspeed);
						modal.animate({
							"opacity" : 0
						}, options.animationspeed, function() {
							modal.css({'opacity' : 1, 'visibility' : 'hidden', 'top' : topMeasure});
							unlockModal();
						});
					}
					if(options.animation == "none") {
						modal.css({'visibility' : 'hidden', 'top' : topMeasure});
						modalBG.css({'display' : 'none'});
					}
				}
			  modal.unbind('briefPopUp:close');
			});

/*---------------------------
 Open and add Closing Listeners
 弹出层然后添加关闭监听事件
----------------------------*/
        	//Open Modal Immediately  立即执行打开事件
			modal.trigger('briefPopUp:open')

			//Close Modal Listeners 关闭按钮绑定关闭事件
			var closeButton = $('.' + options.dismissmodalclass).bind('click.modalEvent', function () {
			    modal.trigger('briefPopUp:close')
			});

			//如果设置为true，添加背景点击事件
			if(options.closeonbackgroundclick) {
				modalBG.css({"cursor":"pointer"})
				modalBG.bind('click.modalEvent', function () {
				    modal.trigger('briefPopUp:close')
				});
			}
			//esc关闭事件
			$('body').keyup(function(e) {
			    if (e.which === 27) { modal.trigger('briefPopUp:close'); } // 27 is the keycode for the Escape key
			});
/*---------------------------
 Animations Locks
 动画锁定
----------------------------*/
			function unlockModal() {
				locked = false;
			}
			function lockModal() {
				locked = true;
			}
        });//each call
    }//orbit plugin call
})(jQuery);

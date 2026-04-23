function Choice(){
  $("#discount > .acc_info").remove();
  var thisVal = $("#dcChoice").val();
  var tags = '';
  if(thisVal == "할인금액"){
    tags += '<div class="acc_info">';
    tags += '<input type="text">';
    tags += '<div class="won_text">';
    tags += '원';
    tags += '</div>';
    tags += '</div>';

    $("#discount").append(tags);

  }else if(thisVal == "할인율"){
    tags += '<div class="acc_info">';
    tags += '<input type="text">';
    tags += '<div class="won_text">';
    tags += '%';
    tags += '</div>';
    tags += '</div>';
    $("#discount").append(tags);
  }
}



function Event(){
  $("#time > .acc_info").remove();
  var thisVal = $("#evChoice").val();
  var tags = '';
  if(thisVal == "즉시 발급"){
    // tags += '<div class="acc_info">';
    // tags += '<input type="text">';
    // tags += '%';
    // tags += '</div>';

    $("#time").append(tags);

  }else if(thisVal == "지정한 시점에 발급"){

    tags += '<div class="acc_info">';
    tags += '<input type="text" id="datepicker1">';
    tags += ' &nbsp; ';
    tags += '<select class="time">';
    tags += '<option value="00">00</option>';
    tags += '<option value="01">01</option>';
    tags += '<option value="02">02</option>';
    tags += '<option value="03">03</option>';
    tags += '<option value="04">04</option>';
    tags += '<option value="05">05</option>';
    tags += '<option value="06">06</option>';
    tags += '<option value="07">07</option>';
    tags += '<option value="08">08</option>';
    tags += '<option value="09">09</option>';
    tags += '<option value="10">10</option>';
    tags += '<option value="11">11</option>';
    tags += '<option value="12">12</option>';
    tags += '<option value="13">13</option>';
    tags += '<option value="14">14</option>';
    tags += '<option value="15">15</option>';
    tags += '<option value="16">16</option>';
    tags += '<option value="17">17</option>';
    tags += '<option value="18">18</option>';
    tags += '<option value="19">19</option>';
    tags += '<option value="20">20</option>';
    tags += '<option value="21">21</option>';
    tags += '<option value="22">22</option>';
    tags += '<option value="23">23</option>';
    tags += '</select>';
    tags += '시';
    tags += ' &nbsp; ';
    tags += '<select class="time">';
    tags += '<option value="00">00</option>';
    tags += '<option value="05">05</option>';
    tags += '<option value="10">10</option>';
    tags += '<option value="15">15</option>';
    tags += '<option value="20">20</option>';
    tags += '<option value="25">25</option>';
    tags += '<option value="30">30</option>';
    tags += '<option value="35">35</option>';
    tags += '<option value="40">40</option>';
    tags += '<option value="45">45</option>';
    tags += '<option value="50">50</option>';
    tags += '<option value="55">55</option>';
    tags += '</select>';

    tags += '분';

    tags += '</div>';
    $("#time").append(tags);
    OnDatePic();
  }
}

function Give(){
  $("#member > .acc_info").remove();
  var thisVal = $("#giChoice").val();
  var tags = '';
  if(thisVal == "대상자 지정 발급"){
    tags += '<div class="acc_info">';
    tags += '<select id="giOption" onchange="Give2()">';
    tags += '<option value="전체회원 발급">전체회원 발급</option>';
    tags += '<option value="특정회원 선택">특정회원 선택</option>';

    tags += '</select>';
    tags += '</div>';

    $("#member").append(tags);
    Give2();

  }else if(thisVal == "조건부 자동 발급"){
    tags += '<div class="acc_info">';
    tags += '<select>';
    tags += '<option value="회원가입 시">회원가입 시</option>';
    tags += '<option value="기념일(생일)">기념일(생일)</option>';
    tags += '<option value="첫 구매고객">첫 구매고객</option>';
    tags += '</select>';
    tags += '</div>';
    $("#member").append(tags);
    Give2();

  }
}


function Give2(){
  var thisOption = $("#giOption").val();

  if(thisOption == "특정회원 선택"){
    $(".vipDiv").addClass("view");
    // var tags = '';
    // tags += '<tr class="addTr">';
    // tags += '<th>';
    // tags += '안녕?'
    // tags += '</th>';
    // tags += '<td>';
    // tags += '안녕'
    // tags += '</td>';
    // tags += '</tr>';
    // $(".popTable tr:nth-child(4)").after(tags);
  }else{
    // $(".popTable tr:nth-child(5).addTr ").remove(tags);
        $(".vipDiv").removeClass("view");
  }
}


function chk1(){

}
function chk2(){

}
function chk3(){

}


function priceSel(){
  $("#price > .acc_info").remove();
  var thisVal = $("#prChoice").val();
  var tags = '';
  if(thisVal == "주문 금액 기준"){
    tags += '<div class="acc_info">';
    tags += '<input type="text">';
    tags += '<div class="won_text">';
    tags += '원 이상 구매시';
    tags += '</div>';
    tags += '</div>';

    $("#price").append(tags);

  }else if(thisVal == "상품 금액 기준"){
    tags += '<div class="acc_info">';
    tags += '<input type="text">';
    tags += '<div class="won_text">';
    tags += '원 이상 구매시';
    tags += '</div>';
    tags += '</div>';
    $("#price").append(tags);
  }
}

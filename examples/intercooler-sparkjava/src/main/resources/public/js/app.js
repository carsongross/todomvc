$(function(){
  $('body').keyup('input', function(e){
    if (e.keyCode === 27) {
      $(e.target).trigger('resetEscape');
    }
  });
  $('body').click(function(e){
    if($('#todo-edit').length > 0 && !$(e.target).is('#todo-edit')){
      $("#edit-form").trigger('submit');
    };
  });
});

Intercooler.ready(function(elt){
  $("[autofocus]:last").focus();
})
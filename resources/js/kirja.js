(function($) {
    $.getJSON('catalog.index', {}, function(data) {
        document.title = data['title'];
        $.each(data['contents'], function(index, chapter) {
            append_chapter_to_toc(chapter);
            append_chapter_to_book(chapter);
        });
    });

    var chapterId = function(chapter) {
        return "chapter_" + chapter.replace('.','_');
    };

    var append_chapter_to_toc = function(chapter) {
        var chapter_id = "#" + chapterId(chapter)
        $("<li>" + chapter + "</li>").appendTo("#contents ul").click(function() {
            $("html").animate({
                scrollTop: $(chapter_id).offset().top
            }, 1000);
        });
    };

    var append_chapter_to_book = function(chapter) {
        var url = chapter.replace('.', '/') + ".html"
        var chapter_id = chapterId(chapter);
        $("<div class='chapter' id='" + chapter_id + "'></div>").appendTo("#book");
        $("#" + chapter_id).load(url);
    };
})(jQuery)

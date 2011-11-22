(function($) {
    $.getJSON('catalog.index', {}, function(data) {
        document.title = data['title'];
        $.each(data['contents'], function(index, chapter) {
            append_chapter_to_toc(chapter);
            append_chapter_to_book(chapter);
        });
    });

    var append_chapter_to_toc = function(chapter) {
        $("<li>" + chapter + "</li>").appendTo("#contents ul");
    };

    var append_chapter_to_book = function(chapter) {
        var url = chapter.replace('.', '/') + ".html"
        $("#book").load(url);
    };
})(jQuery)

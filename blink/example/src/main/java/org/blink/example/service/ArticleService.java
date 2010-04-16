package org.blink.example.service;

import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.blink.example.model.Article;

import com.google.common.collect.Lists;

@ApplicationScoped
public class ArticleService {

    // in order to avoid the need of a persistence layer, I'm storing the data in memory only
    private List<Article> articles = Lists.newArrayList();

    public List<Article> getArticles() {
         return articles;
    }

    public Article createArticle() {
        return new Article();
    }

    public void saveArticle(Article article) {
        if (!articles.contains(article)) {
            article.setDate(Calendar.getInstance().getTime());
            articles.add(article);
        } else {
            // when persisting only in memory there is no need to call anything to update the article
        }
    }

    public void deleteArticle(Article article) {
        articles.remove(article);
    }

}

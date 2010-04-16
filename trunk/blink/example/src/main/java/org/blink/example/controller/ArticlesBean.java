package org.blink.example.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.blink.example.model.Article;
import org.blink.example.service.ArticleService;

@RequestScoped
@Named
public class ArticlesBean {

    private Article currentArticle;

    private List<Article> articles;

    @Inject
    private ArticleService service;

    @PostConstruct
    public void init() {
        articles = service.getArticles();
    }

    public String newArticle() {
        currentArticle = new Article();

        return "article";
    }

    public String saveArticle() {
        service.saveArticle(currentArticle);
        return "articlesList";
    }

    public void deleteArticle() {
        service.deleteArticle(currentArticle);
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public Article getCurrentArticle() {
        return currentArticle;
    }

    public void setCurrentArticle(Article currentArticle) {
        this.currentArticle = currentArticle;
    }
}

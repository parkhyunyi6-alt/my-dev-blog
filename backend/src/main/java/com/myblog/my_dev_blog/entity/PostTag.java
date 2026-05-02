package com.myblog.my_dev_blog.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "post_tags",
    uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "tag_id"})
)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    protected PostTag() {}

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }

    public Long getId() { return id; }
    public Post getPost() { return post; }
    public Tag getTag() { return tag; }
}

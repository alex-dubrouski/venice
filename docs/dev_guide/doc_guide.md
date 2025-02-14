---
layout: default
title: Documentation Guideline
parent: Developer Guides
permalink: /docs/dev_guide/documentation_guideline
---

# Documentation Guideline

We prefer simplicity and currently use GitHub page to host Venice documentation. Those documentation will be built 
automatically by GitHub pipelines in the `main` branch.

## General

It is strongly encouraged that any code change which affects the validity of information in the docs also include 
updates to the docs, so that both are kept in sync atomically.

Experimental functionalities and future plans are also worth documenting, though they must be clearly marked as such, so
that users and operators reading those docs can make informed decisions about the level of risk they are willing to take
on if trying out a given functionality. If the level of maturity of a given functionality is not called out, then it
implicitly means that the functionality is considered mature and its API is unlikely to change. Undocumented configs and
APIs may or may not be considered mature and stable, and if in doubt, it is appropriate to open an Issue to request that
it be explicitly documented.

In general, it is recommended to get familiar with the docs before writing more docs, to try to keep the style and
structure coherent. That being said, even if unsure where some documentation belongs, do err on the side of including it
(anywhere), and reviewers may suggest placing it elsewhere.

## Hierarchy

In order for your docs to be rendered properly in the documentation hierarchy, Venice developers need to add a header
section at the top of each documentation. The `title` section will be what the end user sees in the sidebar, and
the `parent` section represents the parent page of the current page for linking purpose. The `permalink` section will be
the URL path where the page will be served. An example of the header is below:

```
---
layout: default
title: Documentation Guideline
parent: Developer Guides
permalink: /docs/dev_guide/documentation_guideline
---
```

A page in the middle of the hierarchy has both the `parent` and `has_children` attributes. For example:

```
---
layout: default
title: Write APIs
parent: User Guides
has_children: true
permalink: /docs/user_guide/write_api
---
```

For a deeply nested page, a `grand_parent` attribute is also required. For example:

```
---
layout: default
title: Push Job
parent: Write APIs
grand_parent: User Guides
permalink: /docs/user_guide/write_api/push_job
---
```

Note that for now, the doc supports at most 3 levels of nesting.

For more information, consult [Just the Docs](https://just-the-docs.github.io/just-the-docs/docs/navigation-structure/).

## Pictures and Diagrams

It is encouraged to use diagrams within the documentation, but there are some guidelines to standardize the way it is
done, and to avoid certain anti-patterns.

### Text-based Assets

For text-based assets (which are preferred whenever feasible), we wish to check them into source control. This should 
include both the displayable asset (e.g. in SVG format) and the source file from which the displayable asset was 
generated (e.g. in XML format). This makes the docs self-contained, and enables contributors to edit the assets over 
time.

Diagrams conforming to these guidelines can be placed under the `/docs/assets/images` path of the repo, and then 
embedded in the docs with a relative link like this:

```markdown
![](../assets/images/vip_3_read_path.drawio.svg)
```

The [draw.io](https://draw.io) service makes it easy to generate such assets. If using [PlantUML](https://plantuml.com/starting), 
it's recommended to generate diagrams into svg format by following this [guide](https://plantuml.com/svg).

### Binary Assets

For binary assets (e.g. PNG, BMP, JPG, etc.), we do NOT wish to check them into source control. Instead, they should be 
linked from an external source. This can be done in GitHub itself. Within the Pull Request that proposes the doc change, 
the contributor can insert images in the PR's description or comments, and then take the URL GitHub generated for it. 
Then the modified files included in the PR can be edited to link to that image, and the PR updated. Externally hosted 
images can be embedded with an absolute link like this:

```markdown
![](https://user-images.githubusercontent.com/1248632/195111861-518f81c4-f226-4942-b88a-a34337da79e3.png)
```

## Emojis

Here's a link to all the emojis available in README files: [Emoji Cheat Sheet](https://github.com/ikatyang/emoji-cheat-sheet/blob/master/README.md). 
If you want to find a good emoji, you can use [this website](https://emojicombos.com/).

## Testing Doc Changes
A GitHub fork can have its own documentation. This can be setup by:

1. Navigating to the fork's Settings > Pages, i.e.: `https://github.com/<username>/venice/settings/pages`
2. Selecting which branch to publish the docs from.
3. Selecting `/docs` as the root directory.
4. Clicking Save.
5. Navigating to your fork's docs at: `https://<username>.github.io/venice`
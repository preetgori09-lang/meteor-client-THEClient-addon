# Publishing THE Client to GitHub

Target repo: `preetgori09-lang/meteor-client-THEClient-addon`

## 1. Create the empty repo on GitHub

1. Go to https://github.com/new
2. Repository name: `meteor-client-THEClient-addon`
3. Set to **Public** (required — GPL-3.0 means the source must be available to anyone you give the jar to)
4. Do **NOT** initialize with a README, .gitignore or license (we already have them)
5. Create repository

## 2. Push the code

Run these from the **parent** folder (the one containing `THEClient`):

```bash
cd THEClient
git init -b main
git add .
git commit -m "THE Client: redesigned GUI, addon system and Addons tab (based on Meteor Client)"
git remote add origin https://github.com/preetgori09-lang/meteor-client-THEClient-addon.git
git push -u origin main
```

> [!NOTE]
> GitHub may ask you to log in in the browser on first push. If `git` says you're not the
> owner of the repo, make sure the repo name matches exactly and your account has access.

## 3. Check the CI build

1. Open the repo's **Actions** tab
2. The *Build* workflow starts automatically on push
3. When it finishes (a few minutes), the jar is under **Artifacts** as `THE-Client-master-<n>`

## 4. Cut a release

```bash
git tag v1.0.0
git push origin v1.0.0
```

Pushing a `v*` tag makes CI build the jar and attach it to an auto-created GitHub Release with
generated notes. That release page is what you link people to for downloads.

## 5. Polish the repo page (recommended)

- **About** sidebar (gear icon, top right): description `A redesigned GUI for Meteor Client — Minecraft 1.21.11`, topics: `minecraft`, `fabric`, `meteor-client`, `utility-mod`
- **Settings → General → Features**: enable Issues if you want bug reports
- **Settings → Collaborators**: add anyone you want to co-maintain

## 6. Updating later

```bash
git add .
git commit -m "What you changed"
git push            # CI builds automatically
git tag v1.0.1 && git push origin v1.0.1   # only when you want a new release
```

## Checklist before going public

- [x] `LICENSE` is the full GPL-3.0 text (kept from Meteor Client — required)
- [x] README states clearly this is a fork and credits Meteor Development
- [x] `fabric.mod.json` points issues/sources at this fork
- [x] Meteor's private CI secrets/webhooks replaced with a fork-friendly workflow
- [x] Upstream Meteor link and GPL notice intact in README and LICENSE headers
- [ ] Repo is **Public**
- [ ] First release tagged `v1.0.0`

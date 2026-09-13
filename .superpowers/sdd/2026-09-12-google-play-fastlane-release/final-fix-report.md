# Google Play Fastlane release final-fix report

## Final review fix wave

### Status

Implemented the complete final-review finding set on
`feature/release-candidate-preparation`. The publish job now accepts only a new,
non-deleted, non-forced `v*` tag push or an operator-initiated
`workflow_dispatch` whose selected ref is a matching tag. Every external action
in the secret-bearing job is pinned to a full upstream commit SHA, and all
three release-evidence files are checked individually for non-empty content
before upload.

### Changes

- Replaced the broad tag-ref condition with an outer matching-tag requirement
  and an explicit event branch:
  - `push` requires `created == true`, `deleted == false`, and
    `forced == false`;
  - `workflow_dispatch` remains available for a matching-tag retry.
- Pinned all six external action invocations in `publish-internal`, retaining an
  adjacent exact release-version comment for human review.
- Added a pre-upload shell loop that checks the AAB, R8 mapping, and checksum
  with `-s`, failing on a missing or empty file before `upload-artifact` runs.
- Reworked the workflow test to parse YAML and inspect the
  `publish-internal` job rather than relying on workflow-wide substrings. It
  now covers the trigger truth conditions, both gates, environment, exact
  read-only permissions, exactly five secret names, every immutable action
  reference and comment, locked Fastlane invocation, exact artifact name and
  three paths, `if-no-files-found`, 30-day retention, per-file checks, cleanup,
  and disposable-tag exclusion.
- Documented the active `v*` tag ruleset as a release prerequisite, including
  restricted creation, updates, and deletion plus a narrow release-actor bypass
  list. Updated trigger documentation to distinguish automatic new-tag pushes
  from manual matching-tag retries.
- No metadata-validation behavior was changed and no metadata tests were added;
  the final review identified no concrete metadata gap.

### TDD evidence

The production changes that made the new tests pass were the guarded job
condition, immutable action refs, and the named per-file verification step.

#### RED

Command, run after modifying only
`fastlane/test/android_ci_workflow_test.rb`:

```text
ruby fastlane/test/android_ci_workflow_test.rb
```

Exit code: `1`

Exact output:

```text
Run options: --seed 54180

# Running:

F.....FF

Finished in 0.023530s, 339.9915 runs/s, 722.4819 assertions/s.

  1) Failure:
AndroidCiWorkflowTest#test_publishes_only_new_safe_tag_pushes_or_manual_matching_tag_retries [fastlane/test/android_ci_workflow_test.rb:49]:
--- expected
+++ actual
@@ -1,3 +1,3 @@
 # encoding: UTF-8
 #    valid: true
-"startsWith(github.ref, 'refs/tags/v') && ( ( github.event_name == 'push' && github.event.created == true && github.event.deleted == false && github.event.forced == false ) || github.event_name == 'workflow_dispatch' )"
+"startsWith(github.ref, 'refs/tags/v')"


  2) Failure:
AndroidCiWorkflowTest#test_checks_each_release_artifact_before_upload [fastlane/test/android_ci_workflow_test.rb:129]:
Missing publish-internal step named "Verify release artifacts"

  3) Failure:
AndroidCiWorkflowTest#test_pins_every_external_action_to_a_full_commit_sha_with_version_comment [fastlane/test/android_ci_workflow_test.rb:72]:
--- expected
+++ actual
@@ -1 +1 @@
-["actions/checkout@11d5960a326750d5838078e36cf38b85af677262", "gradle/actions/wrapper-validation@748248ddd2a24f49513d8f472f81c3a07d4d50e1", "actions/setup-java@cf277c60eb25467037889841efdb72551f06f6c3", "gradle/actions/setup-gradle@748248ddd2a24f49513d8f472f81c3a07d4d50e1", "ruby/setup-ruby@95ef2b042f9d7a56d8268cba8559e2842e2ad01b", "actions/upload-artifact@ea165f8d65b6e75b540449e92b4886f43607fa02"]
+["actions/checkout@v4", "gradle/actions/wrapper-validation@v4", "actions/setup-java@v4", "gradle/actions/setup-gradle@v4", "ruby/setup-ruby@v1", "actions/upload-artifact@v4"]


8 runs, 17 assertions, 3 failures, 0 errors, 0 skips
```

All three failures were the intended missing-behavior failures; there were no
load, parse, or test errors.

#### GREEN

Command, run after the minimal workflow implementation:

```text
ruby fastlane/test/android_ci_workflow_test.rb
```

Exit code: `0`

Exact output:

```text
Run options: --seed 17881

# Running:

........

Finished in 0.010820s, 739.3715 runs/s, 4805.9149 assertions/s.

8 runs, 52 assertions, 0 failures, 0 errors, 0 skips
```

### Pinned-action provenance

Resolved on 2026-09-13 from each action's official GitHub repository and exact
release tag. `git ls-remote` supplied the full object IDs; the official release
pages independently show the matching short commit and release identity.

Commands:

```text
git ls-remote https://github.com/actions/checkout.git refs/tags/v4.4.0
git ls-remote https://github.com/actions/setup-java.git refs/tags/v4.9.1
git ls-remote https://github.com/actions/upload-artifact.git refs/tags/v4.6.2
git ls-remote https://github.com/gradle/actions.git refs/tags/v4.4.4 'refs/tags/v4.4.4^{}'
git ls-remote https://github.com/ruby/setup-ruby.git refs/tags/v1.321.0
```

Exact output:

```text
11d5960a326750d5838078e36cf38b85af677262	refs/tags/v4.4.0
cf277c60eb25467037889841efdb72551f06f6c3	refs/tags/v4.9.1
ea165f8d65b6e75b540449e92b4886f43607fa02	refs/tags/v4.6.2
da187c8e6ffbd3802e00f2477aa5a822b25f2dda	refs/tags/v4.4.4
748248ddd2a24f49513d8f472f81c3a07d4d50e1	refs/tags/v4.4.4^{}
95ef2b042f9d7a56d8268cba8559e2842e2ad01b	refs/tags/v1.321.0
```

| Action | Release provenance | Pinned commit |
| --- | --- | --- |
| `actions/checkout` | [v4.4.0](https://github.com/actions/checkout/releases/tag/v4.4.0) | `11d5960a326750d5838078e36cf38b85af677262` |
| `gradle/actions/wrapper-validation` | [v4.4.4](https://github.com/gradle/actions/releases/tag/v4.4.4) | `748248ddd2a24f49513d8f472f81c3a07d4d50e1` |
| `actions/setup-java` | [v4.9.1](https://github.com/actions/setup-java/releases/tag/v4.9.1) | `cf277c60eb25467037889841efdb72551f06f6c3` |
| `gradle/actions/setup-gradle` | [v4.4.4](https://github.com/gradle/actions/releases/tag/v4.4.4) | `748248ddd2a24f49513d8f472f81c3a07d4d50e1` |
| `ruby/setup-ruby` | [v1.321.0](https://github.com/ruby/setup-ruby/releases/tag/v1.321.0) | `95ef2b042f9d7a56d8268cba8559e2842e2ad01b` |
| `actions/upload-artifact` | [v4.6.2](https://github.com/actions/upload-artifact/releases/tag/v4.6.2) | `ea165f8d65b6e75b540449e92b4886f43607fa02` |

The Gradle release uses a signed annotated tag, so
`da187c8e6ffbd3802e00f2477aa5a822b25f2dda` is the tag object and the workflow
correctly pins its peeled commit,
`748248ddd2a24f49513d8f472f81c3a07d4d50e1`. Both Gradle action paths come from
that same release commit.

### Verification

Commands:

```text
ruby fastlane/test/android_ci_workflow_test.rb && ruby fastlane/test/release_support_test.rb
actionlint .github/workflows/android-ci.yml
ruby -e 'require "yaml"; YAML.safe_load_file(".github/workflows/android-ci.yml", aliases: true); puts "YAML parse OK"'
ruby -c fastlane/test/android_ci_workflow_test.rb
ruby -c fastlane/release_support.rb
ruby -c fastlane/Fastfile
git diff --check
```

Observed output:

```text
8 runs, 52 assertions, 0 failures, 0 errors, 0 skips
8 runs, 41 assertions, 0 failures, 0 errors, 0 skips
YAML parse OK
Syntax OK
Syntax OK
Syntax OK
```

All commands exited `0`. `actionlint` and `git diff --check` produced no output.

### Files

- `.github/workflows/android-ci.yml`
- `fastlane/test/android_ci_workflow_test.rb`
- `fastlane/README.md`
- `.superpowers/sdd/2026-09-12-google-play-fastlane-release/final-fix-report.md`

### Self-review

- The matching-tag predicate wraps both event branches, so manual dispatch from
  a branch cannot publish. The push branch contains every required webhook flag
  and cannot accept a tag update, deletion, or force push.
- The test derives every security-sensitive assertion from the parsed
  `publish-internal` job or its isolated source block. A matching token in
  another job can no longer create a false pass.
- The action assertion compares the entire ordered external-action set and then
  separately enforces a 40-character lowercase hexadecimal ref and exact
  adjacent version comment for each entry.
- The secret assertion compares the complete unique secret-name set, not only
  two representative values, and job permissions must equal only
  `{ contents: read }`.
- Artifact assertions cover the exact name, ordered three-file list,
  `if-no-files-found: error`, 30-day retention, and a preceding `-s` check over
  each required path.
- Changes are confined to the reviewed workflow, its focused test, release
  operator documentation, and this report. No Gradle, Fastlane lane, metadata,
  store asset, or unrelated file was modified.

### Concerns

- No live GitHub tag event or Google Play upload was attempted. Repository
  administrators must create and verify the documented active `v*` tag ruleset
  and existing `google-play` Environment before the first end-to-end release.
- Commit pinning intentionally stops automatic action updates. Each future
  action upgrade must re-resolve the official release tag to a reviewed full
  commit SHA and update both the workflow and focused expectations.

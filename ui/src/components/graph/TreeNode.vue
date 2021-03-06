<template>
    <div class="node-wrapper">
        <div class="status-color" v-if="n.taskRun" :class="contentCls"></div>
        <div class="task-content">
            <div class="card-header">
                <div class="icon-wrapper">
                    <!-- <img src=""/> -->
                </div>
                <div class="task-title">
                    <span>{{task.id | ellipsis(18)}}</span>
                </div>
                <!-- <menu-open class="node-action" @click="onSettings" /> -->
            </div>
            <div v-if="task.state" class="status-wrapper">
                <status :status="state" />
            </div>
            <div class="info-wrapper">
                <div :title="task.type" v-if="!childrenCount" class="pull task-item">
                    <console title />
                </div>
                <div v-else :title="$t('stream')" class="pull task-item">
                    <current-ac title />
                </div>
                <b-button
                    v-if="childrenCount"
                    :title="`${$t('display direct sub tasks count')} : ${childrenCount}`"
                    class="node-action push"
                    @click="onFilterGroup"
                >
                    <graph title />
                </b-button>

                <b-button
                    v-if="!isFlow"
                    :disabled="!hasLogs"
                    class="node-action"
                    :title="$t('show task logs')"
                >
                    <router-link
                        :title="$t('show task logs')"
                        v-if="hasLogs"
                        class="btn-secondary"
                        :to="{name:'execution', params: $route.params, query: {tab:'logs', search: task.id}}"
                    >
                        <format-list-checks title />
                    </router-link>
                    <format-list-checks v-else title />
                </b-button>
                <b-button
                    v-if="!isFlow"
                    :disabled="!hasOutputs"
                    class="node-action"
                    :title="$t('show task outputs')"
                >
                    <router-link
                        v-if="hasOutputs"
                        class="btn-secondary"
                        :title="$t('show task outputs')"
                        :to="{name:'execution', params: $route.params, query: {tab:'execution-output', search: task.id + (value ? ` - ${value}` : '')}}"
                    >
                        <location-exit title />
                    </router-link>
                    <location-exit v-else title />
                </b-button>
                <b-button
                    class="node-action"
                    size="sm"
                    v-b-modal="`modal-${hash}`"
                    :title="$t('show task source')"
                >
                    <code-tags />
                </b-button>
            </div>
        </div>

        <b-modal :hide-footer="true" :id="`modal-${hash}`" :title="`Task ${task.id}`">
            <pre>{{taskYaml}}</pre>
        </b-modal>
    </div>
</template>
<script>
import Console from "vue-material-design-icons/Console";
import Graph from "vue-material-design-icons/Graph";
import CodeTags from "vue-material-design-icons/CodeTags";
import FormatListChecks from "vue-material-design-icons/FormatListChecks";
import LocationExit from "vue-material-design-icons/LocationExit";
import CurrentAc from "vue-material-design-icons/CurrentAc";
import { mapState } from "vuex";
import Status from "../Status";
import Yaml from "yaml";

export default {
    components: {
        Status,
        Console,
        Graph,
        CodeTags,
        FormatListChecks,
        LocationExit,
        CurrentAc
    },
    props: {
        n: {
            type: Object,
            default: undefined
        },
        isFlow: {
            type: Boolean,
            default: false
        }
    },
    methods: {
        onFilterGroup() {
            this.$emit("onFilterGroup", this.task.id);
        },
        onSettings() {
            if (this.node) {
                this.$store.dispatch("graph/setNode", undefined);
            } else {
                this.$store.dispatch("graph/setNode", this.n);
                this.$emit("onSettings");
            }
        }
    },
    computed: {
        ...mapState("graph", ["node"]),
        hasLogs() {
            return (
                this.attempts.filter(attempt => attempt.logs.length).length > 0
            );
        },
        hasOutputs() {
            return this.n.taskRun && this.n.taskRun.outputs;
        },
        attempts() {
            return this.n.taskRun && this.n.taskRun.attempts
                ? this.n.taskRun.attempts
                : [];
        },
        hash() {
            return this.task.id.hashCode();
        },
        childrenCount() {
            return this.task.tasks ? this.task.tasks.length : 0;
        },
        taskYaml() {
            return Yaml.stringify(this.n);
        },
        state() {
            return this.n.taskRun ? this.n.taskRun.state.current : "SUCCESS";
        },
        contentCls() {
            return {
                "is-success": !["RUNNING", "FAILED"].includes(this.state),
                "is-running": this.state === "RUNNING",
                "is-failed": this.state === "FAILED"
            };
        },
        task() {
            return this.n.task;
        },
        value () {
            return this.n.taskRun && this.n.taskRun.value
        }
    }
};
</script>
<style scoped lang="scss">
@import "../../styles/_variable.scss";
.wrapper.is-container {
    border: 2px dashed $purple;
    border-radius: 2px;
}
.node-wrapper {
    cursor: pointer;
    display: flex;

    .status-color {
        width: 4px;
        height: 55px;
        border: 0;
    }

    .is-success {
        background-color: $green;
    }
    .is-running {
        background-color: $blue;
    }
    .is-failed {
        background-color: $red;
    }

    .task-content {
        flex-grow: 1;
        background-color: $white;

        .card-header {
            height: 30px;
            padding: 2px;
            margin: 0px;
            border-bottom: 1px solid $gray-500;
            background: $gray-200;
            flex: 1;
            flex-wrap: nowrap;

            .icon-wrapper {
                display: inline-block;
                flex-shrink: 2;
                img {
                    width: 16px;
                    height: 16px;
                }
            }

            .task-title {
                margin-left: 5px;
                display: inline-block;
                font-size: $font-size-sm;
                flex-grow: 1;
            }
            .node-action {
                flex-shrink: 2;
                padding-top: 18px;
                padding-right: 18px;
            }
        }
        .status-wrapper {
            margin: 10px;
        }
    }

    .card-wrapper {
        top: 50px;
        position: absolute;
    }
    .info-wrapper {
        display: flex;
        justify-content: right;
    }
    .push {
        margin-left: auto;
    }
    .pull {
        margin-right: auto;
    }
    .node-action {
        height: 25px;
        padding-top: 1px;
        padding-right: 5px;
        padding-left: 5px;
    }
    .task-item {
        margin-left: 5px;
        margin-bottom: 5px;
    }
}
</style>
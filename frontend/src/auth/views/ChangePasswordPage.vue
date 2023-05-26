<template>
  <v-container class="page-login">
    <v-row justify="center" style="margin: 40px auto">
      <v-img alt="Logo" class="shrink" src="@/assets/logo.svg" width="450" />
    </v-row>
    <v-row>
      <v-col :cols="12">
        <v-card class="pa-3 page-login__card" tile>
          <v-card-title class="justify-center my-2">
            <div class="display-1">Change Password</div>
          </v-card-title>
          <v-card-text class="mb-2">
            <v-form ref="form" v-model="formValid" class="mt-5" lazy-validation>
              <v-text-field
                v-model="oldPassword"
                append-icon="mdi-lock"
                autocomplete="off"
                name="oldPassword"
                label=""
                placeholder="Old Password"
                type="password"
                :rules="formRule.password"
                @input="handleInput"
                required
                outlined
              />
              <v-text-field
                v-model="newPassword"
                append-icon="mdi-lock"
                autocomplete="off"
                name="newPassword"
                label=""
                placeholder="New Password"
                type="password"
                :rules="formRule.password"
                @input="handleInput"
                required
                outlined
              />
              <v-text-field
                v-model="confirmPassword"
                append-icon="mdi-lock"
                autocomplete="off"
                name="confirmPassword"
                label=""
                placeholder="Confirm Password"
                type="password"
                :rules="confirmPasswordRules"
                @input="handleInput"
                required
                outlined
              />
            </v-form>
            <v-alert :type="type" v-if="message">
              <v-row align="center">
                {{ message }}
              </v-row>
            </v-alert>
          </v-card-text>

          <v-card-actions>
            <v-btn text class="overline grey--text" @click="handleCancel">
              Cancel
            </v-btn>
            <v-spacer />
            <v-btn rounded color="primary" :loading="loading" @click="handlePasswordChange">
              <div class="mx-8">Submit</div>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import AuthService from '../services/auth.service';

export default {
  data() {
    return {
      type: 'success',
      message: '',
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
      submitted: false,
      loading: false,
      formValid: false,
      formRule: {
        password: [v => !!v || 'password required']
      }
    };
  },

  computed: {
    confirmPasswordRules() {
      const rules = [this.newPassword === this.confirmPassword || 'Password must match.'];
      return rules;
    }
  },

  methods: {
    handlePasswordChange() {
      if (this.$refs.form.validate()) {
        this.loading = true;
        this.submitted = true;
        const { oldPassword, newPassword } = this;
        if (oldPassword && newPassword) {
          // Call service to change password
          AuthService.changePassword(oldPassword, newPassword)
            .then(
              data => {
                this.type = 'success';
                this.message = data.message;
                setTimeout(() => {
                  this.$router.push('/', () => {});
                }, 3000);
              },
              error => {
                this.type = 'error';
                this.message = (error.data && error.data.message) || 'Error occurred';
              }
            )
            .finally(() => {
              this.loading = false;
            });
        }
      }
    },

    handleCancel() {
      this.$router.go(-1);
    },

    handleInput() {
      this.message = '';
    }
  }
};
</script>

<style lang="scss" scoped>
.page-login__card {
  max-width: 600px;
  margin: 0 auto;
}
</style>
